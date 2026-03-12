import pandas as pd
import requests
from io import StringIO

GFZ_URL = "https://kp.gfz.de/app/files/Kp_ap_Ap_SN_F107_since_1932.txt"

def fetch_model_data(n_lags=27) -> pd.DataFrame:
    """
    Fetch GFZ data and build features strictly matching:
    ["F10.7obs", f107_lag_1 ... f107_lag_n]
    Returns: date + feature columns
    """

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

    df["date"] = pd.to_datetime(df[["year","month","day"]])

    df = df[["date", "F10.7obs"]].dropna()

    # Create lag features
    for lag in range(1, n_lags + 1):
        df[f"f107_lag_{lag}"] = df["F10.7obs"].shift(lag)

    df = df.dropna().reset_index(drop=True)

    feature_cols = ["F10.7obs"] + [f"f107_lag_{i}" for i in range(1, n_lags + 1)]

    # Return date + features (consistent contract)
    df = df[["date"] + feature_cols]

    print(f"Final dataset with {len(df)} rows and {len(df.columns)} columns.")
    return df