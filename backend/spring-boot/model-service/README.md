# Model Service (Flask + LightGBM)

## Overview
The **Model Service** is a Python FastAPI microservice that loads a serialized **LightGBM** model (`.pkl`) and exposes REST endpoints for solar flux prediction.  
It serves as the inference layer for the **Spring Boot backend**, using metadata stored in **PostgreSQL** to validate input features and run predictions.
---

## Architecture

```
Spring Boot Backend
       │
       ▼
Model Service (FastAPI)
 ├── Connects → PostgreSQL (for model + feature metadata)
 └── Loads → LightGBM model (.pkl)
       │
       ▼
Returns F10.7 flux predictions (JSON)
```

---

##  Tech Stack
| Component | Version / Tool |
|------------|----------------|
| Python | 3.12 |
| Framework | FastAPI |
| ML Library | LightGBM |
| Serialization | Joblib |
| Database | PostgreSQL |
| Container | Docker + Uvicorn |

---


## Key Files

| File | Description |
|------|--------------|
| `app.py` | FastAPI application entrypoint |
| `models/lgb_f107_lag27_ap_lag3.pkl` | Serialized LightGBM model |
| `requirements.txt` | Python dependencies |
| `Dockerfile` | Container definition for model-service |

---

## 🚀 Endpoints

### `GET /`
Health check.
```json
{"status": "ok", "model": "lgb_f107_lag27_ap_lag3"}
```

### `GET /models`
Lists all models registered in the database.
```json
{
  "count": 1,
  "models": [
    {
      "model_id": "lgb_f107_lag27_ap_lag3",
      "family": "lightgbm",
      "description": "LightGBM model using 27-day lagged F10.7 and 3-day lagged Ap indices",
      "created_at": "2025-11-07T00:00:00Z"
    }
  ]
}
```

### `GET /predict/{model_id}/{horizon_days}`
Returns model metadata and required features.
```json
{
  "model_id": "lgb_f107_lag27_ap_lag3",
  "horizon_days": 1,
  "required_features": ["f107_lag_1", "f107_lag_2", "...", "ap_max_lag3"]
}
```

### `POST /predict/{model_id}/{horizon_days}`
Runs a prediction using provided feature data.
```json
{
  "features": {
    "f107_lag_1": 145.2,
    "f107_lag_2": 143.8,
    "...": "...",
    "ap_max_lag3": 21.0
  }
}
```

**Response**
```json
{
  "model_id": "lgb_f107_lag27_ap_lag3",
  "horizon_days": 1,
  "predicted_flux": 142.99
}
```

---

# 🧠 How It Works

1. On startup:
    - Loads `lgb_f107_lag27_ap_lag3.pkl` from `/models`.
    - Connects to Postgres to fetch `model_features` for that model.
    - Logs `Loaded <model_id> with <N> features.`

2. On a prediction request:
    - Validates the incoming JSON against required features.
    - Converts feature values to a NumPy array.
    - Performs inference using the LightGBM model.
    - Returns the numeric prediction as JSON.

3. On database connection delay:
    - Retries connection multiple times.
    - Fails gracefully with an HTTP 500 if Postgres isn’t ready.

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
uvicorn app:app --host 0.0.0.0 --port 5000
```

Open docs at: 👉 http://localhost:5000/docs

---

## Dockerized Deployment

### Build and run container
```bash
docker compose build model-service
docker compose up model-service
```

**Logs**
```
Loaded lgb_f107_lag27_ap_lag3 with 36 features.
INFO:     Uvicorn running on http://0.0.0.0:5000
```

---

## Integration Notes
- The **Spring Boot backend** sends requests to:
  ```
  http://model-service:5000/predict/lgb_f107_lag27_ap_lag3/1
  ```
- The **fetch-service** provides live feature data via:
  ```
  http://fetch-service:5500/latest
  ```
- Both communicate internally over Docker’s `app-net` bridge.

---

## Example Workflow
1. `fetch-service` returns latest features.
2. `model-service` validates and predicts the next F10.7 value.
3. `Spring Boot` collects and returns the prediction to the client.
4. Optionally, the prediction is saved to the `predictions` table in Postgres.

---

## Credits
Developed by the **CSULA Aerospace Senior Design Team (2025–2026)**  
Lead Advisor: *Dr. Zilong Ye*  
Purpose: F10.7 Solar Flux Forecasting for Space Weather Modeling
