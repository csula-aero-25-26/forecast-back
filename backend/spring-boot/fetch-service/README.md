# Fetch Service (FastAPI + Data Preprocessing)

## Version: *3.0.0*

## Overview

The **Fetch Service** is a Python FastAPI microservice responsible for
retrieving and preprocessing raw solar data from GFZ.

It builds a **canonical, model-agnostic feature set** consisting of:

- F10.7 observed flux
- Sunspot number (SN)
- 27 lagged F10.7 values

The service exposes the most recent feature vector via `/latest`.
It does not write to the database. Feature caching and persistence
are handled by the Spring Boot backend.
---

## Architecture

```
Spring Boot Backend
       │
       ▼
Fetch Service (FastAPI)
 ├── Downloads (cached) → GFZ dataset
 ├── Builds → Canonical feature superset
 └── Returns → Latest feature vector (JSON)
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
| Libraries | Pandas, Requests |
| Container | Docker + Uvicorn |
---

## Key Files


| File | Description |
|------|--------------|
| `app.py` | FastAPI application entrypoint |
| `requirements.txt` | Python dependencies |
| `Dockerfile` | Container definition |
| `README.md` | Documentation |

---

## Endpoints

### `GET /`
Health check.
```json
{"status": "ok", "service": "fetch-service"}
```

### `GET /latest`

Returns the most recent canonical feature row.
```json
{
  "date": "2026-03-12",
  "features": {
    "F10.7obs": 122.8,
    "SN": 110,
    "f107_lag_1": 126.9,
    "f107_lag_2": 128.4,
    ...
    "f107_lag_27": 129.0
  }
}
```

### `GET /ground-truths`

Returns historical F10.7 solar flux data.

Supports optional query parameters:
- `days` — number of most recent days to return
- `start_date` — inclusive start date (`YYYY-MM-DD`)
- `solar_cycles` — number of most recent solar cycles

Only one parameter may be used at a time.

#### Examples

```bash
/ground-truths?days=30
```

```bash
/ground-truths?start_date=2024-01-01
```

```bash
/ground-truths?solar_cycles=1
```

#### Response

```json
[
  {
    "observation_date": "2026-03-12",
    "actual_flux": 122.8
  }
]
```

---

# How It Works

1. The service downloads or retrieves the latest GFZ dataset.
2. The raw GFZ dataset is cached in memory for 12 hours to avoid repeated downloads.
3. It extracts F10.7 observed flux and sunspot number (SN).
4. It generates 27 lagged F10.7 features.
5. It returns the most recent row as JSON.

The feature set is model-agnostic.
Individual models select the subset of features they require.

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

- The Spring Boot backend calls:
  http://fetch-service:5500/latest

- The backend is responsible for:
    - Feature caching (features_daily table)
    - Prediction persistence
    - Database interactions

- The Fetch Service is stateless with short-lived in-memory caching (12-hour TTL) and does not write to PostgreSQL.

---

## Example Workflow

1. Backend calls `/latest` to retrieve canonical features.
2. Backend checks if features for the date are cached.
3. If not cached, backend stores them in `features_daily`.
4. Backend sends features to the model-service.
5. Model-service performs inference and returns predicted flux.

---

## Credits
Developed by the **CSULA Aerospace Senior Design Team (2025–2026)**  
Lead Advisor: *Dr. Zilong Ye*  
Purpose: F10.7 Solar Flux Forecasting — Data Acquisition and Feature Generation Layer
