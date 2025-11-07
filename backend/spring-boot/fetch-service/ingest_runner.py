import re
import pandas as pd
from sqlalchemy import create_engine
from models.lgb_f107_lag27_ap_lag3 import fetch_model_data
from db_utils import write_features_to_db


DATABASE_URL = "postgresql://aspteam:asp20252026@localhost:5332/forecastdb"

_MODEL_ID_PATTERN = re.compile(r"^[A-Za-z0-9._\-]+$")

def get_model_config(engine, model_id: str) -> dict:
    if not _MODEL_ID_PATTERN.match(model_id):
        raise ValueError("Invalid model_id format. Use letters, numbers, dashes, underscores, or dots.")

    sql = "SELECT * FROM model_registry WHERE model_id = %s;"
    df = pd.read_sql_query(sql, engine, params=(model_id,))

    if df.empty:
        raise LookupError(f"Model '{model_id}' not found in model_registry.")
    return df.iloc[0].to_dict()

def get_model_features(engine, model_id: str) -> pd.DataFrame:
    sql = """
          SELECT f.name, f.source, f.transformation, f.description
          FROM model_features mf
                   JOIN feature_catalog f ON mf.feature_name = f.name
          WHERE mf.model_id = %s; \
          """
    df = pd.read_sql_query(sql, engine, params=(model_id,))

    if df.empty:
        print(f"No features found for model '{model_id}'.")
    else:
        print(f"Found {len(df)} features for model '{model_id}'.")
    return df


def main():
    engine = create_engine(DATABASE_URL)
    model_id = "lgb-f107-lag27-ap-lag3"

    # Load model config
    cfg = get_model_config(engine, model_id)
    print("Loaded model config:")
    for k, v in cfg.items():
        print(f"  {k}: {v}")

    # Load model features
    features = get_model_features(engine, model_id)
    if not features.empty:
        print(features.head())

    # Fetch and preview GFZ data
    print("\nFetching model data...")
    df = fetch_model_data()

    print("\nSample of parsed data:")
    print(df.head())

    write_features_to_db(engine, df, 'features_daily')

if __name__ == "__main__":
    main()
