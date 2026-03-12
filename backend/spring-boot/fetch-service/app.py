from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
import pandas as pd
from datetime import datetime

from feature_builders.persistence import fetch_model_data as fetch_persistence
from feature_builders.flux_27_lags import fetch_model_data as fetch_flux_27_lags
from feature_builders.flux_27_lags_ssn import fetch_model_data as fetch_flux_27_lags_ssn


app = FastAPI(
    title="Fetch Service API",
    description="Builds model-specific feature sets from GFZ data.",
    version="2.0"
)

PIPELINES = {
    "persistence": fetch_persistence,
    "linreg_flux_27_lags_ssn": fetch_flux_27_lags_ssn,
    "lgbm_flux_27_lags": fetch_flux_27_lags,
    "xgb_flux_27_lags": fetch_flux_27_lags
}


@app.get("/latest/{model_id}")
def get_latest_features(model_id: str):

    base_model = model_id.split("_horizon_")[0]

    if base_model not in PIPELINES:
        raise HTTPException(status_code=404, detail="Model pipeline not supported")

    df = PIPELINES[base_model]()

    if df.empty:
        raise HTTPException(status_code=404, detail="No data available")

    latest = df.iloc[-1].to_dict()

    current_date = datetime.now().strftime("%Y-%m-%d")

    return JSONResponse(content={
        "model_id": model_id,
        "date": current_date,
        "features": latest
    })


@app.get("/")
def root():
    return {"status": "ok", "service": "fetch-service"}
