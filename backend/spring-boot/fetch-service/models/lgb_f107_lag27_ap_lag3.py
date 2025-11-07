import pandas as pd
import requests
from io import StringIO

GFZ_URL = "https://kp.gfz.de/app/files/Kp_ap_Ap_SN_F107_since_1932.txt"

def fetch_model_data() -> pd.DataFrame:
    """Fetch GFZ data and build all features for LightGBM model (F10.7 lags + Ap lags)."""
    print("Downloading GFZ data...")
    r = requests.get(GFZ_URL)
    r.raise_for_status()

    cols = [
        "year","month","day","days","days_m","Bsr","dB",
        "Kp1","Kp2","Kp3","Kp4","Kp5","Kp6","Kp7","Kp8",
        "ap1","ap2","ap3","ap4","ap5","ap6","ap7","ap8",
        "Ap","SN","F107obs","F107adj","D"
    ]
    df = pd.read_csv(StringIO(r.text), sep='\\s+', comment="#", names=cols, engine="python")

    df["date"] = pd.to_datetime(df[["year","month","day"]])
    df["ap_mean"] = df[[f"ap{i}" for i in range(1,9)]].mean(axis=1)
    df["ap_max"]  = df[[f"ap{i}" for i in range(1,9)]].max(axis=1)

    # keep only relevant columns
    df = df[["date","ap_mean","ap_max","F107adj"]].rename(columns={"F107adj":"fluxadjflux"})
    df = df.dropna()

    # --- create lag features ---
    for lag in range(1, 28):
        df[f"f107_lag_{lag}"] = df["fluxadjflux"].shift(lag)

    for lag in range(1, 4):
        df[f"ap_mean_lag{lag}"] = df["ap_mean"].shift(lag)
        df[f"ap_max_lag{lag}"]  = df["ap_max"].shift(lag)

    # target = flux 7 days ahead
    df["target_flux"] = df["fluxadjflux"].shift(-7)

    df = df.dropna().reset_index(drop=True)
    print(f"Final dataset with {len(df)} rows and {len(df.columns)} columns.")
    print(df.head())
    return df

if __name__ == "__main__":
    df = fetch_model_data()
    df.to_csv("f107_ap_features.csv", index=False)
