# Model Service (FastAPI + Multi-Model Inference)

This folder contains the **FastAPI-based model service** used by the Spring Boot backend to perform real-time F10.7 solar flux predictions.  
The service dynamically loads serialized model files (`{model_id}.pkl`) and retrieves feature definitions from PostgreSQL exported with **Joblib**, exposing them through a REST API endpoint (`/predict/{model_id}`).

---

## Project Structure
```
model-service/
│
├── app.py          # FastAPI application serving the model
├── models/
├──── lgb_f107_lag27_ap_lag3_horizon_1.pkl
├──── linreg_flux_27_lags_ssn_horizon_7.pkl
├──── persistence_horizon_7.pkl
├── requirements.txt          # Python dependencies
├── Dockerfile                # Container build for model service
└── README.md                 # This file
```

---

## Features
- Loads and serves the trained LightGBM model for inference.  
- Exposes a `/predict/{model_id}` POST endpoint that accepts JSON input and returns a predicted F10.7 value.  
- Integrated with **Docker Compose** for containerized deployment.  
- Communicates with the **Spring Boot backend** via HTTP (JSON).  

---

## How It Works
1. A request is made to `/predict/{model_id}`.
2. The service loads `{model_id}.pkl` dynamically.
3. The required feature list is retrieved from PostgreSQL (`model_features` table).
4. Input features are validated.
5. The model performs inference and returns predicted flux.

---

## Local Setup (without Docker)

### 1. Create a virtual environment within `model-service/`
```bash
python -m venv .venv
```

### 2. Activate the environment
- **Windows PowerShell**
  ```bash
  .venv\Scripts\activate
  ```
- **Linux / macOS**
  ```bash
  source .venv/bin/activate
  ```

### 3. Install dependencies
```bash
pip install -r requirements.txt
```

### 4. Run the app
```bash
python app.py
```

The service will start on **http://localhost:5000**

---

## Running with Docker Compose

The `model-service` is included as part of the project’s main `docker-compose.yml`.  
To build and start the containerized services (PostgreSQL + Model Service):

```bash
docker compose up --build
```

This will:
- Build the FastAPI model image (`spring-boot-model-service`)  
- Start the container (`model-service`)  
- Expose the service at `http://localhost:5000/predict/{model_id}`

---

## Example Request
Example POST request to the `/predict/{model_id}` endpoint:
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{
  "features": {
    "F10.7obs": 150,
    "f107_lag_1": 149,
    "f107_lag_2": 148,
    ...
    "SN": 120
  }
}' \
http://127.0.0.1:5000/predict/linreg_flux_27_lags_ssn_horizon_7
```
## Example 2
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{
"lags": [150,149,148,147,146,145,144,143,142,141,140,139,138,137,136,135,134,133,132,131,130,129,128,127,126,125,124],
"ap_mean": 15,
"ap_max": 30,
"ap_mean_lag1": 14,
"ap_mean_lag2": 14,
"ap_mean_lag3": 13,
"ap_max_lag1": 28,
"ap_max_lag2": 29,
"ap_max_lag3": 31
}' http://localhost:8080/api/input/predict/{model_id}
```

---

## Supported Models

- lgb_f107_lag27_ap_lag3_horizon_1
- linreg_flux_27_lags_ssn_horizon_7
- persistence_horizon_7

---

## Integration Notes
- The Spring Boot backend calls this service at:
  http://localhost:5000/predict/{model_id}
  (inside Docker Compose, using the container name)
---

## Dependencies
- Python 3.12+
- FastAPI  
- Joblib  
- LightGBM  
- NumPy  

---

## Credits
Developed as part of the **CSULA Aerospace Senior Design (Fall 2025 - Spring 2026)** project for F10.7 solar flux forecasting.  
