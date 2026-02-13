import pandas as pd
import requests
from io import StringIO

GFZ_URL = "https://kp.gfz.de/app/files/Kp_ap_Ap_SN_F107_since_1932.txt"

def fetch_model_data(n_lags=27) -> pd.DataFrame:
    """Fetch GFZ data and build features for Linear Regression (F10.7 lags + SN)."""

    print("Downloading GFZ data...")
    r = requests.get(GFZ_URL)
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

    # Keep only what LinReg was trained on
    df = df[["date", "F10.7obs", "SN"]].dropna()

    # Create lag features for F10.7obs
    for lag in range(1, n_lags + 1):
        df[f"f107_lag_{lag}"] = df["F10.7obs"].shift(lag)

    df = df.dropna().reset_index(drop=True)

    print(f"Final dataset with {len(df)} rows and {len(df.columns)} columns.")
    return df


if __name__ == "__main__":
    df = fetch_model_data()
    df.to_csv("f107_linreg_features.csv", index=False)
