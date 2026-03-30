from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import JSONResponse
import pandas as pd
import requests
from io import StringIO
from datetime import datetime, timedelta

app = FastAPI(
    title="Fetch Service API",
    description="Stateless fetch service that retrieves GFZ solar activity data (F10.7 flux), generates model features, and provides filtered ground truth data by time range or solar cycles.",
    version="3.0"
)

GFZ_URL = "https://kp.gfz.de/app/files/Kp_ap_Ap_SN_F107_since_1932.txt"

# --- Cache ---
GFZ_RAW_CACHE = None
GFZ_LAST_FETCH = None

CACHE_TTL = timedelta(minutes=1)


# GFZ RAW FETCH (CACHED)
def fetch_gfz_raw() -> str:
    global GFZ_RAW_CACHE, GFZ_LAST_FETCH

    now = datetime.utcnow()

    if (
            GFZ_RAW_CACHE is not None and
            GFZ_LAST_FETCH is not None and
            now - GFZ_LAST_FETCH < CACHE_TTL
    ):
        print("Retrieving Cached GFZ data...")
        return GFZ_RAW_CACHE

    print("Downloading GFZ data...")
    r = requests.get(GFZ_URL, timeout=30)
    r.raise_for_status()

    GFZ_RAW_CACHE = r.text
    GFZ_LAST_FETCH = now

    return GFZ_RAW_CACHE

# MODEL DATA
def fetch_model_data(n_lags=27) -> pd.DataFrame:
    raw = fetch_gfz_raw()

    cols = [
        "year","month","day","days","days_m","Bsr","dB",
        "Kp1","Kp2","Kp3","Kp4","Kp5","Kp6","Kp7","Kp8",
        "ap1","ap2","ap3","ap4","ap5","ap6","ap7","ap8",
        "Ap","SN","F10.7obs","F10.7adj","D"
    ]

    df = pd.read_csv(
        StringIO(raw),
        sep=r"\s+",
        comment="#",
        names=cols,
        engine="python"
    )

    df = df[["F10.7obs", "SN"]].dropna()

    for lag in range(1, n_lags + 1):
        df[f"f107_lag_{lag}"] = df["F10.7obs"].shift(lag)

    df = df.dropna().reset_index(drop=True)

    return df

# SOLAR CYCLES
def fetch_solar_cycles() -> pd.DataFrame:
    df = pd.read_csv(
        "data/SILSO_cycles_minmax.csv",
        sep=";",
        encoding="latin1",
        skiprows=1,
        header=0
    )

    df.columns = [
        "cycle",
        "min_year", "min_month", "min_sn",
        "max_year", "max_month", "max_sn"
    ]

    df = df.dropna(subset=["cycle"])
    df["cycle"] = df["cycle"].astype(int)

    df["start_date"] = pd.to_datetime(
        dict(year=df["min_year"], month=df["min_month"], day=1)
    )

    df["end_date"] = df["start_date"].shift(-1)

    return df.sort_values("cycle")[["cycle", "start_date", "end_date"]]

# GROUND TRUTHS
def fetch_ground_truths() -> pd.DataFrame:
    raw = fetch_gfz_raw()

    cols = [
        "year","month","day","days","days_m","Bsr","dB",
        "Kp1","Kp2","Kp3","Kp4","Kp5","Kp6","Kp7","Kp8",
        "ap1","ap2","ap3","ap4","ap5","ap6","ap7","ap8",
        "Ap","SN","F10.7obs","F10.7adj","D"
    ]

    df = pd.read_csv(
        StringIO(raw),
        sep=r"\s+",
        comment="#",
        names=cols,
        engine="python"
    )

    df = df[df["F10.7obs"] > 0]

    df["observation_date"] = pd.to_datetime(
        df[["year", "month", "day"]]
    )

    df = df[["observation_date", "F10.7obs"]].rename(
        columns={"F10.7obs": "actual_flux"}
    )

    return df

# ENDPOINTS
@app.get(
    "/latest",
    summary="Get latest feature set",
    description="Returns the most recent F10.7-derived feature set for model inference.",
    tags=["Features"]
)
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


@app.get("/ground-truths", summary="Retrieve historical F10.7 solar flux data",
         description="Returns historical F10.7 solar flux data with optional filtering by recent days, start date, or number of solar cycles.", tags=["Ground Truth"])
def get_ground_truths(
        days: int = Query(
            None,
            description="Number of most recent days to return",
            example=365
        ),
        start_date: str = Query(
            None,
            description="Start date (inclusive) in YYYY-MM-DD format",
            example="2024-01-01"
        ),
        solar_cycles: int = Query(
            None,
            description="Number of most recent solar cycles to include",
            example=1
        )
):
    """
    Retrieve F10.7 solar flux data.

    Supports:
    - last N days
    - data since a given date
    - last N solar cycles

    Only one filter may be used at a time.
    """
    df = fetch_ground_truths()

    if df.empty:
        raise HTTPException(status_code=404, detail="No data available")

    params_used = sum([
        days is not None,
        start_date is not None,
        solar_cycles is not None
    ])

    if params_used > 1:
        raise HTTPException(
            status_code=400,
            detail="Use only one of 'days', 'start_date', or 'solar_cycles'"
        )

    # --- solar cycles ---
    if solar_cycles is not None:
        cycles_df = fetch_solar_cycles()

        if solar_cycles <= 0:
            raise HTTPException(400, detail="'solar_cycles' must be > 0")

        if solar_cycles > len(cycles_df):
            raise HTTPException(
                400,
                detail=f"Requested solar_cycles exceeds available data ({len(cycles_df)} cycles)"
            )

        latest_cycle = cycles_df["cycle"].max()
        min_cycle = latest_cycle - solar_cycles + 1

        valid_cycles = cycles_df[cycles_df["cycle"] >= min_cycle]
        cutoff = valid_cycles["start_date"].min()

        df = df[df["observation_date"] >= cutoff].copy()

    # --- start date ---
    elif start_date is not None:
        try:
            start = pd.to_datetime(start_date)
        except Exception:
            raise HTTPException(400, detail="Invalid 'start_date' format (use YYYY-MM-DD)")

        df = df[df["observation_date"] >= start].copy()

    # --- days ---
    elif days is not None:
        if days <= 0:
            raise HTTPException(400, detail="'days' must be > 0")

        df = df.tail(days).copy()

    # --- default ---
    else:
        df = df.tail(365).copy()

    df["observation_date"] = df["observation_date"].dt.strftime("%Y-%m-%d")

    return JSONResponse(content=df.to_dict(orient="records"))


@app.get("/", summary="Health check")
def root():
    return {"status": "ok", "service": "fetch-service"}