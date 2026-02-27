# from fastapi import FastAPI, HTTPException
# from fastapi.responses import JSONResponse
# import pandas as pd
# from models.lgb_f107_lag27_ap_lag3 import fetch_model_data
#
# # Create FastAPI object
# app = FastAPI(
#     title="Fetch Service",
#     description="Downloads and processes GFZ solar flux data for model-service consumption.",
#     version="1.0.0"
# )
#
# # Expose /latest endpoint
# @app.get("/latest")
# def get_latest_features():
#     """Return the most recent feature row for model-service."""
#     try:
#         # obtains a dataframe of the model-formatted features
#         df = fetch_model_data()
#
#         if df.empty:
#             raise HTTPException(status_code=404, detail="No data available")
#         # Grabs the most recent day (ideally today) for prediction
#         latest = df.iloc[-1]
#         # Drop columns not needed for inference
#         features = latest.drop(labels=["date", "target_flux"], errors="ignore").to_dict()
#
#         return JSONResponse(content={"features": features})
#
#     except Exception as e:
#         raise HTTPException(status_code=500, detail=f"Error fetching latest features: {str(e)}")
#
# # At root of endpoint, returns status
# @app.get("/")
# def root():
#     """Health check endpoint."""
#     return {"status": "ok", "service": "fetch-service"}
#
#

from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse

from models.lgb_f107_lag27_ap_lag3 import fetch_model_data as fetch_lgb
from models.linreg_flux_27_lags_ssn import fetch_model_data as fetch_linreg
from models.persistence import fetch_model_data as fetch_persistence

app = FastAPI(
    title="Fetch Service",
    description="Builds model-specific feature sets from GFZ data.",
    version="1.0.1"
)

PIPELINES = {
    "lgb_f107_lag27_ap_lag3": fetch_lgb,
    "linreg_flux_27_lags_ssn": fetch_linreg,
    "persistence": fetch_persistence,
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

    # Remove non-feature fields
    latest.pop("date", None)
    latest.pop("target_flux", None)

    return JSONResponse(content={
        "model_id": model_id,
        "features": latest
    })


@app.get("/")
def root():
    return {"status": "ok", "service": "fetch-service"}
