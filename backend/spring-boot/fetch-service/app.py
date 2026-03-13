from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
import pandas as pd
import requests
from io import StringIO
from datetime import datetime

app = FastAPI(
    title="Fetch Service API",
    description="Builds canonical feature set from GFZ data.",
    version="2.0"
)

GFZ_URL = "https://kp.gfz.de/app/files/Kp_ap_Ap_SN_F107_since_1932.txt"


def fetch_model_data(n_lags=27) -> pd.DataFrame:
    print("Downloading GFZ data...")
    r = requests.get(GFZ_URL, timeout=30)
    r.raise_for_status()

    cols = [
        "year","month","day","days","days_m","Bsr","dB",
        "Kp1","Kp2","Kp3","Kp4","Kp5","Kp6","Kp7","Kp8",
        "ap1","ap2","ap3","ap4","ap5","ap6","ap7","ap8",
        "Ap","SN","F10.7obs","F10.7adj","D"
    ]

    df = pd.read_csv(
        StringIO(r.text),
        sep=r"\s+",
        comment="#",
        names=cols,
        engine="python"
    )

    # Always include SN
    df = df[["F10.7obs", "SN"]].dropna()

    for lag in range(1, n_lags + 1):
        df[f"f107_lag_{lag}"] = df["F10.7obs"].shift(lag)

    df = df.dropna().reset_index(drop=True)

    return df


@app.get("/latest")
def get_latest_features():

    df = fetch_model_data()

    if df.empty:
        raise HTTPException(status_code=404, detail="No data available")

    latest = df.iloc[-1].to_dict()
    current_date = datetime.utcnow().strftime("%Y-%m-%d")

    return JSONResponse(content={
        "date": current_date,
        "features": latest
    })

@app.get("/")
def root():
    return {"status": "ok", "service": "fetch-service"}