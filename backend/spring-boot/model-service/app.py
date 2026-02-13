from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import time
import joblib
import psycopg2
import numpy as np

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
# FastAPI app
# --------------------------------------------------
app = FastAPI(
    title="Model Service API",
    description="Handles dynamic model inference and registry access.",
    version="2.2"
)

# --------------------------------------------------
# Schemas
# --------------------------------------------------
class FeaturePayload(BaseModel):
    features: dict

# --------------------------------------------------
# Database utilities
# --------------------------------------------------
def get_feature_list(model_id: str, retries=5, delay=5):
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
            cur.close()
            conn.close()

            if not features:
                raise RuntimeError(f"No features found for model_id {model_id}")

            return features

        except Exception as e:
            print(f"DB connection attempt {attempt} failed: {e}")
            if attempt == retries:
                raise HTTPException(status_code=500, detail="Database connection failed")
            time.sleep(delay)


def load_model_and_features(model_id: str):
    model_path = f"models/{model_id}.pkl"

    try:
        bundle = joblib.load(model_path)
    except Exception:
        raise HTTPException(status_code=404, detail="Model file not found")

    features = get_feature_list(model_id)

    # Case 1: Raw model (LGBM)
    if hasattr(bundle, "predict"):
        return bundle, features

    # Case 2: Bundled model dict (linreg)
    if isinstance(bundle, dict) and "model" in bundle:
        return bundle["model"], features

    # Case 3: Persistence
    if isinstance(bundle, dict) and bundle.get("model_type") == "persistence":

        def persistence_predict(X):
            # X is 2D array, first feature is F10.7obs
            return np.array([X[0][0]])

        return persistence_predict, features

    raise HTTPException(status_code=500, detail="Unknown model format")

# --------------------------------------------------
# Endpoints
# --------------------------------------------------
@app.get("/")
def root():
    return {"status": "ok"}


@app.get("/models")
def list_models():
    conn = psycopg2.connect(**DB_CONFIG)
    cur = conn.cursor()
    cur.execute("""
                SELECT model_id, family, description, created_at
                FROM model_registry
                ORDER BY created_at DESC;
                """)
    rows = cur.fetchall()
    cur.close()
    conn.close()

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


@app.get("/predict/{model_id}")
def predict_info(model_id: str):

    model, features = load_model_and_features(model_id)

    return {
        "model_id": model_id,
        "message": "Send POST to this endpoint with {'features': {...}}",
        "required_features": features
    }

@app.post("/predict/{model_id}")
def predict_model(model_id: str, payload: FeaturePayload):

    model, features = load_model_and_features(model_id)

    data = payload.features
    missing = [f for f in features if f not in data]
    if missing:
        raise HTTPException(status_code=400, detail=f"Missing features: {missing}")

    feats = [float(data[f]) for f in features]
    X = np.array(feats).reshape(1, -1)

    y_pred = float(model.predict(X)[0]) if hasattr(model, "predict") else float(model(X)[0])

    return {
        "model_id": model_id,
        "predicted_flux": y_pred
    }
