def write_features_to_db(engine, df, table_name):
    df.to_sql(table_name, engine, if_exists="replace", index=False)
    print(f"Wrote {len(df)} rows to {table_name}.")
