# Fetch Service (FastAPI + Data Preprocessing)

## Overview
The **Fetch Service** is a Python FastAPI microservice responsible for **retrieving and processing raw solar and geomagnetic data**.  
It builds model-ready feature datasets and provides the most recent feature row to the **Model Service** for prediction.  
This service supports multiple model-specific feature generation scripts (e.g., `lgb_f107_lag27_ap_lag3_horizon_1.py`), allowing different forecasting models to be added or updated without changing the service’s structure.

---

## Architecture

```
Spring Boot Backend
       │
       ▼
Fetch Service (FastAPI)
 ├── Downloads → Raw Dataset (e.g. Kp_ap_Ap_SN_F107_since_1932.txt)
 ├── Generates → Feature sets via model-specific .py scripts
 └── (Optional) Writes → features_daily table in PostgreSQL
       │
       ▼
Returns latest processed feature vector (JSON)
```

---

## Tech Stack
| Component | Version / Tool |
|------------|----------------|
| Python | 3.12 |
| Framework | FastAPI |
| Libraries | Pandas, Requests, SQLAlchemy |
| Database | PostgreSQL |
| Container | Docker + Uvicorn |

---

## Key Files

| File | Description |
|------|--------------|
| `app.py` | FastAPI application entrypoint |
| `models/lgb_f107_lag27_ap_lag3.py` | Example feature generation script for a specific model |
| `models/<future_model>.py` | Placeholder for future model-specific feature builders |
| `ingest_runner.py` | Executes full dataset ingestion and writes to `features_daily` |
| `db_utils.py` | Utility for writing DataFrames to PostgreSQL via SQLAlchemy |
| `requirements.txt` | Python dependencies |
| `Dockerfile` | Container definition for fetch-service |

---

## Endpoints

### `GET /`
Health check.
```json
{"status": "ok", "service": "fetch-service"}
```

### `GET /latest/{model_id}`

Returns the **most recent** feature row for the specified model.
```json
{
  "model_id": "lgb_f107_lag27_ap_lag3_horizon_1",
  "features": {
    "f107_lag_1": 145.2,
    "f107_lag_2": 143.8,
    "...": "...",
    "ap_mean_lag3": 5.25,
    "ap_max_lag3": 15.0
  }
}
```

---

# How It Works

1. **Model-specific feature generation**  
   Each model has a corresponding Python file in `/models` (e.g., `lgb_f107_lag27_ap_lag3_horizon_1.py`) that defines how its features are constructed from external input dataset.  
   Additional models can be added by following the same pattern; the service dynamically imports and executes the correct script.

2. **`/latest/{model_id}` Endpoint**  
   When called, the endpoint:
    - Downloads the latest input data file.
    - Cleans and formats it into a Pandas DataFrame.
    - Builds features as defined by the active model script.
    - Returns only the most recent row as JSON.

3. **Batch Mode (`ingest_runner.py`)**  
   When run manually, this script:
    - Generates the full feature dataset.
    - Writes it to the `features_daily` table in PostgreSQL using SQLAlchemy.

4. **Database Alignment**  
   Feature names are designed to align with:
    - `feature_catalog`
    - `model_features` for each model entry in the database.

---

## Local Development

### Create and activate virtual environment
```bash
python -m venv .venv
.venv\Scripts\activate  # Windows
source .venv/bin/activate  # macOS/Linux
```

### Install dependencies
```bash
pip install -r requirements.txt
```

### Run locally
```bash
uvicorn app:app --host 0.0.0.0 --port 5500
```

Open documentation at: http://localhost:5500/docs

---

## Dockerized Deployment

### Build and run container
```bash
docker compose build fetch-service
docker compose up fetch-service
```

**Logs**
```
Final dataset: 34,000 rows × 36 columns
INFO:     Uvicorn running on http://0.0.0.0:5500
```

---

## Integration Notes
- The **Spring Boot backend** calls this service at:
  ```
  http://fetch-service:5500/latest/{model_id}
  ```
- The **Model Service** consumes this JSON response and uses it for prediction:
  ```
  http://model-service:5000/predict/{model_id}
  ```
- Both microservices communicate internally using Docker’s `app-net` bridge.

---

## Example Workflow
1. `fetch-service` downloads and preprocesses the raw GFZ data.
2. It exposes the most recent feature vector through `/latest`.
3. `model-service` retrieves those features and performs inference.
4. The **Spring Boot backend** coordinates the call chain and returns results to the client.

---

## Credits
Developed by the **CSULA Aerospace Senior Design Team (2025–2026)**  
Lead Advisor: *Dr. Zilong Ye*  
Purpose: F10.7 Solar Flux Forecasting — Data Acquisition and Feature Generation Layer
