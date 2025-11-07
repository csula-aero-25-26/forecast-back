from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
import pandas as pd
from models.lgb_f107_lag27_ap_lag3 import fetch_model_data

app = FastAPI(
    title="Fetch Service",
    description="Downloads and processes GFZ solar flux data for model-service consumption.",
    version="1.0.0"
)

@app.get("/latest")
def get_latest_features():
    """Return the most recent feature row for model-service."""
    try:
        df = fetch_model_data()  # assumes this returns a pandas DataFrame
        if df.empty:
            raise HTTPException(status_code=404, detail="No data available")

        latest = df.iloc[-1]  # last (most recent) day
        # Drop columns not needed for inference
        features = latest.drop(labels=["date", "target_flux"], errors="ignore").to_dict()

        return JSONResponse(content={"features": features})

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching latest features: {str(e)}")


@app.get("/")
def root():
    """Health check endpoint."""
    return {"status": "ok", "service": "fetch-service"}