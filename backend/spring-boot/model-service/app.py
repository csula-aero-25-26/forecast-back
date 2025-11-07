from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import time, joblib, psycopg2, numpy as np

# --------------------------------------------------
# Database configuration
# --------------------------------------------------
DB_CONFIG = {
    "host": "db",
    "port": 5432,
    "dbname": "forecastdb",
    "user": "aspteam",
    "password": "asp20252026"
}

# --------------------------------------------------
# Model configuration
# --------------------------------------------------
MODEL_ID = "lgb_f107_lag27_ap_lag3"
MODEL_PATH = f"models/{MODEL_ID}.pkl"

def get_feature_list(model_id: str, retries=5, delay=5):
    """Fetch ordered feature names with retry if DB not ready."""
    for attempt in range(1, retries + 1):
        try:
            conn = psycopg2.connect(**DB_CONFIG)
            cur = conn.cursor()
            cur.execute("""
                        SELECT feature_name
                        FROM model_features
                        WHERE model_id = %s
                        ORDER BY feature_name;
                        """, (model_id,))
            features = [r[0] for r in cur.fetchall()]
            cur.close(); conn.close()
            if not features:
                raise RuntimeError(f"No features found for model_id {model_id}")
            return features
        except Exception as e:
            print(f"DB connection attempt {attempt} failed: {e}")
            if attempt == retries:
                raise
            time.sleep(delay)
# --------------------------------------------------
# Load model and feature metadata
# --------------------------------------------------
try:
    model = joblib.load(MODEL_PATH)
    FEATURES = get_feature_list(MODEL_ID)
    print(f"Loaded {MODEL_ID} with {len(FEATURES)} features.")
except Exception as e:
    print(f"Startup error: {e}")
    FEATURES, model = [], None

# --------------------------------------------------
# FastAPI app
# --------------------------------------------------
app = FastAPI(
    title="Model Service API",
    description="Handles model inference and registry access.",
    version="2.1"
)

# --------------------------------------------------
# Schemas
# --------------------------------------------------
class FeaturePayload(BaseModel):
    """Expected request body containing model features."""
    features: dict

# --------------------------------------------------
# Endpoints
# --------------------------------------------------
@app.get("/")
def root():
    """Health check."""
    return {"status": "ok", "model": MODEL_ID}

@app.get("/models")
def list_models():
    """List all registered models."""
    conn = psycopg2.connect(**DB_CONFIG)
    cur = conn.cursor()
    cur.execute("""
                SELECT model_id, family, description, created_at
                FROM model_registry
                ORDER BY created_at DESC;
                """)
    rows = cur.fetchall()
    cur.close(); conn.close()

    return {
        "count": len(rows),
        "models": [
            {
                "model_id": r[0],
                "family": r[1],
                "description": r[2],
                "created_at": r[3]
            } for r in rows
        ]
    }

@app.get("/predict/{model_id}/{horizon_days}")
def predict_info(model_id: str, horizon_days: int):
    """Describe how to call the model prediction endpoint."""
    if model_id != MODEL_ID:
        raise HTTPException(status_code=404, detail=f"Model '{model_id}' not available")
    if model is None:
        raise HTTPException(status_code=500, detail="Model not loaded")

    return {
        "model_id": model_id,
        "horizon_days": horizon_days,
        "message": "Send a POST to /predict/{model_id}/{horizon_days} with a JSON body: {'features': {...}}",
        "required_features": FEATURES
    }

@app.post("/predict/{model_id}/{horizon_days}")
def predict_model(model_id: str, horizon_days: int, payload: FeaturePayload):
    """Run prediction for the given model_id and horizon_days using provided features."""
    if model_id != MODEL_ID:
        raise HTTPException(status_code=404, detail=f"Model '{model_id}' not available")
    if model is None:
        raise HTTPException(status_code=500, detail="Model not loaded")

    data = payload.features
    missing = [f for f in FEATURES if f not in data]
    if missing:
        raise HTTPException(status_code=400, detail=f"Missing features: {missing}")

    # Build feature vector in DB-defined order
    feats = [float(data[f]) for f in FEATURES]
    X = np.array(feats).reshape(1, -1)

    # Run prediction
    y_pred = float(model.predict(X)[0])
    return {
        "model_id": model_id,
        "horizon_days": horizon_days,
        "predicted_flux": y_pred
    }
