-- ==========================================
-- CSULA Aerospace Senior Design – DB Initialization
-- Core tables for model tracking and forecasts
-- ==========================================

-- Model Registry --
CREATE TABLE IF NOT EXISTS model_registry (
    model_id     TEXT PRIMARY KEY,              -- e.g. 'rf-v2-aplags'
    family       TEXT NOT NULL,                 -- e.g. 'random_forest', 'lstm'
    description  TEXT,                          -- short notes (optional)
    created_at   TIMESTAMPTZ DEFAULT NOW()
);
-- Remove deprecated model
DELETE FROM model_registry
WHERE model_id = 'lgb_f107_lag27_ap_lag3_horizon_1';

-- <Model Family> | Features: <schema> | Horizon: <X days> | Validation: <method>
INSERT INTO model_registry (model_id, family, description)
VALUES (
           'linreg_flux_27_lags_ssn_horizon_1',
           'linear_regression',
           'Linear Regression | Features: F10.7obs + f107_lag_1-27 + SN | Horizon: 1 day | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;

INSERT INTO model_registry (model_id, family, description)
VALUES (
           'linreg_flux_27_lags_ssn_horizon_7',
           'linear_regression',
           'Linear Regression | Features: F10.7obs + f107_lag_1-27 + SN | Horizon: 7 days | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;

INSERT INTO model_registry (model_id, family, description)
VALUES (
           'persistence_horizon_1',
           'baseline',
           'Persistence Baseline | Features: F10.7obs | Horizon: 1 day | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;


INSERT INTO model_registry (model_id, family, description)
VALUES (
           'persistence_horizon_7',
           'baseline',
           'Persistence Baseline | Features: F10.7obs | Horizon: 7 days | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;

INSERT INTO model_registry (model_id, family, description)
VALUES (
           'lgbm_flux_27_lags_horizon_1',
           'lightgbm',
           'LightGBM | Features: F10.7obs + f107_lag_1-27 | Horizon: 1 day | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;


INSERT INTO model_registry (model_id, family, description)
VALUES (
           'lgbm_flux_27_lags_horizon_7',
           'lightgbm',
           'LightGBM | Features: F10.7obs + f107_lag_1-27 | Horizon: 7 days | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;


INSERT INTO model_registry (model_id, family, description)
VALUES (
           'xgb_flux_27_lags_horizon_1',
           'xgboost',
           'XGBoost | Features: F10.7obs + f107_lag_1-27 | Horizon: 1 day | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;


INSERT INTO model_registry (model_id, family, description)
VALUES (
           'xgb_flux_27_lags_horizon_7',
           'xgboost',
           'XGBoost | Features: F10.7obs + f107_lag_1-27 | Horizon: 7 days | Validation: Walk-forward evaluation'
       )
ON CONFLICT (model_id) DO UPDATE
SET description = EXCLUDED.description;

-- ️Predictions --
CREATE TABLE IF NOT EXISTS predictions (
    id               BIGSERIAL PRIMARY KEY,
    requested_at     TIMESTAMPTZ DEFAULT NOW(),
    prediction_date  DATE DEFAULT CURRENT_DATE,
    horizon_days     INT NOT NULL,
    target_date      DATE,  -- <— now a plain column
    predicted_value  DOUBLE PRECISION NOT NULL,
    model_id         TEXT REFERENCES model_registry(model_id) ON DELETE SET NULL,
    features         JSONB DEFAULT '{}'::jsonb,
    notes            TEXT
);

ALTER TABLE predictions ADD CONSTRAINT unique_model_target UNIQUE (model_id, target_date);

-- Ground Truths --
CREATE TABLE IF NOT EXISTS ground_truths (
    observation_date DATE PRIMARY KEY,
    actual_flux      DOUBLE PRECISION NOT NULL,
    source_meta      JSONB DEFAULT '{}'::jsonb
);

-- Feature Catalog (Metadata) --
CREATE TABLE IF NOT EXISTS feature_catalog (
    feature_id      BIGSERIAL PRIMARY KEY,
    name            TEXT UNIQUE NOT NULL,
    source          TEXT,
    transformation  TEXT,
    description     TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

INSERT INTO feature_catalog (name, source, transformation, description)
VALUES
    -- F107 lags
    ('F10.7obs','NOAA_F107','current','Observed daily F10.7 solar flux'),
    ('f107_lag_1',  'NOAA_F107', 'lag(1)',  'F10.7 value 1 day ago'),
    ('f107_lag_2',  'NOAA_F107', 'lag(2)',  'F10.7 value 2 days ago'),
    ('f107_lag_3',  'NOAA_F107', 'lag(3)',  'F10.7 value 3 days ago'),
    ('f107_lag_4',  'NOAA_F107', 'lag(4)',  'F10.7 value 4 days ago'),
    ('f107_lag_5',  'NOAA_F107', 'lag(5)',  'F10.7 value 5 days ago'),
    ('f107_lag_6',  'NOAA_F107', 'lag(6)',  'F10.7 value 6 days ago'),
    ('f107_lag_7',  'NOAA_F107', 'lag(7)',  'F10.7 value 7 days ago'),
    ('f107_lag_8',  'NOAA_F107', 'lag(8)',  'F10.7 value 8 days ago'),
    ('f107_lag_9',  'NOAA_F107', 'lag(9)',  'F10.7 value 9 days ago'),
    ('f107_lag_10', 'NOAA_F107', 'lag(10)', 'F10.7 value 10 days ago'),
    ('f107_lag_11', 'NOAA_F107', 'lag(11)', 'F10.7 value 11 days ago'),
    ('f107_lag_12', 'NOAA_F107', 'lag(12)', 'F10.7 value 12 days ago'),
    ('f107_lag_13', 'NOAA_F107', 'lag(13)', 'F10.7 value 13 days ago'),
    ('f107_lag_14', 'NOAA_F107', 'lag(14)', 'F10.7 value 14 days ago'),
    ('f107_lag_15', 'NOAA_F107', 'lag(15)', 'F10.7 value 15 days ago'),
    ('f107_lag_16', 'NOAA_F107', 'lag(16)', 'F10.7 value 16 days ago'),
    ('f107_lag_17', 'NOAA_F107', 'lag(17)', 'F10.7 value 17 days ago'),
    ('f107_lag_18', 'NOAA_F107', 'lag(18)', 'F10.7 value 18 days ago'),
    ('f107_lag_19', 'NOAA_F107', 'lag(19)', 'F10.7 value 19 days ago'),
    ('f107_lag_20', 'NOAA_F107', 'lag(20)', 'F10.7 value 20 days ago'),
    ('f107_lag_21', 'NOAA_F107', 'lag(21)', 'F10.7 value 21 days ago'),
    ('f107_lag_22', 'NOAA_F107', 'lag(22)', 'F10.7 value 22 days ago'),
    ('f107_lag_23', 'NOAA_F107', 'lag(23)', 'F10.7 value 23 days ago'),
    ('f107_lag_24', 'NOAA_F107', 'lag(24)', 'F10.7 value 24 days ago'),
    ('f107_lag_25', 'NOAA_F107', 'lag(25)', 'F10.7 value 25 days ago'),
    ('f107_lag_26', 'NOAA_F107', 'lag(26)', 'F10.7 value 26 days ago'),
    ('f107_lag_27', 'NOAA_F107', 'lag(27)', 'F10.7 value 27 days ago'),

    -- Ap features (mean, max, and lag variants)
    ('ap_mean', 'NOAA_AP', 'current', 'Ap mean (current day)'),
    ('ap_max', 'NOAA_AP', 'current', 'Ap max (current day)'),
    ('ap_mean_lag1', 'NOAA_AP', 'lag(1)', 'Ap mean 1 day ago'),
    ('ap_max_lag1', 'NOAA_AP', 'lag(1)', 'Ap max 1 day ago'),
    ('ap_mean_lag2', 'NOAA_AP', 'lag(2)', 'Ap mean 2 days ago'),
    ('ap_max_lag2', 'NOAA_AP', 'lag(2)', 'Ap max 2 days ago'),
    ('ap_mean_lag3', 'NOAA_AP', 'lag(3)', 'Ap mean 3 days ago'),
    ('ap_max_lag3', 'NOAA_AP', 'lag(3)', 'Ap max 3 days ago'),

    -- SSN features
    ('SN', 'SILSO', 'current', 'Daily international sunspot number')
ON CONFLICT (name) DO NOTHING;


-- Model Features (Links Features to Models, Lookup) --
CREATE TABLE IF NOT EXISTS model_features (
    model_id TEXT REFERENCES model_registry(model_id),
    feature_name TEXT REFERENCES feature_catalog(name),
    PRIMARY KEY (model_id, feature_name)
);

INSERT INTO model_features (model_id, feature_name)
SELECT 'linreg_flux_27_lags_ssn_horizon_1', name
FROM feature_catalog
WHERE name IN (
    'F10.7obs', 'f107_lag_1','f107_lag_2','f107_lag_3','f107_lag_4','f107_lag_5',
    'f107_lag_6','f107_lag_7','f107_lag_8','f107_lag_9','f107_lag_10',
    'f107_lag_11','f107_lag_12','f107_lag_13','f107_lag_14','f107_lag_15',
    'f107_lag_16','f107_lag_17','f107_lag_18','f107_lag_19','f107_lag_20',
    'f107_lag_21','f107_lag_22','f107_lag_23','f107_lag_24','f107_lag_25',
    'f107_lag_26','f107_lag_27',
    'SN'
)
ON CONFLICT (model_id, feature_name) DO NOTHING;

INSERT INTO model_features (model_id, feature_name)
SELECT 'linreg_flux_27_lags_ssn_horizon_7', name
FROM feature_catalog
WHERE name IN (
               'F10.7obs', 'f107_lag_1','f107_lag_2','f107_lag_3','f107_lag_4','f107_lag_5',
               'f107_lag_6','f107_lag_7','f107_lag_8','f107_lag_9','f107_lag_10',
               'f107_lag_11','f107_lag_12','f107_lag_13','f107_lag_14','f107_lag_15',
               'f107_lag_16','f107_lag_17','f107_lag_18','f107_lag_19','f107_lag_20',
               'f107_lag_21','f107_lag_22','f107_lag_23','f107_lag_24','f107_lag_25',
               'f107_lag_26','f107_lag_27',
               'SN'
    )
ON CONFLICT (model_id, feature_name) DO NOTHING;


INSERT INTO model_features (model_id, feature_name)
VALUES
    ('persistence_horizon_1','F10.7obs')
ON CONFLICT DO NOTHING;

INSERT INTO model_features (model_id, feature_name)
VALUES
    ('persistence_horizon_7','F10.7obs')
ON CONFLICT DO NOTHING;



-- Features Daily (Actual Features Used by Model) --
CREATE TABLE IF NOT EXISTS features_daily (
    date DATE PRIMARY KEY,
    flux DOUBLE PRECISION,
    adjflux DOUBLE PRECISION,
    target_flux DOUBLE PRECISION,
    lag1 DOUBLE PRECISION,
    lag2 DOUBLE PRECISION,
    lag3 DOUBLE PRECISION,
    lag4 DOUBLE PRECISION,
    lag5 DOUBLE PRECISION,
    lag6 DOUBLE PRECISION,
    lag7 DOUBLE PRECISION,
    lag8 DOUBLE PRECISION,
    lag9 DOUBLE PRECISION,
    lag10 DOUBLE PRECISION,
    lag11 DOUBLE PRECISION,
    lag12 DOUBLE PRECISION,
    lag13 DOUBLE PRECISION,
    lag14 DOUBLE PRECISION,
    lag15 DOUBLE PRECISION,
    lag16 DOUBLE PRECISION,
    lag17 DOUBLE PRECISION,
    lag18 DOUBLE PRECISION,
    lag19 DOUBLE PRECISION,
    lag20 DOUBLE PRECISION,
    lag21 DOUBLE PRECISION,
    lag22 DOUBLE PRECISION,
    lag23 DOUBLE PRECISION,
    lag24 DOUBLE PRECISION,
    lag25 DOUBLE PRECISION,
    lag26 DOUBLE PRECISION,
    lag27 DOUBLE PRECISION,
    ap_mean DOUBLE PRECISION,
    ap_max DOUBLE PRECISION,
    ap_mean_lag1 DOUBLE PRECISION,
    ap_max_lag1 DOUBLE PRECISION,
    ap_mean_lag2 DOUBLE PRECISION,
    ap_max_lag2 DOUBLE PRECISION,
    ap_mean_lag3 DOUBLE PRECISION,
    ap_max_lag3 DOUBLE PRECISION
);


-- ==========================================
-- Indexes
-- ==========================================
CREATE INDEX IF NOT EXISTS idx_predictions_model_id
    ON predictions(model_id);

CREATE INDEX IF NOT EXISTS idx_predictions_target_date
    ON predictions(target_date);

CREATE INDEX IF NOT EXISTS idx_ground_truths_date
    ON ground_truths(observation_date);

