# Model Service (Flask + LightGBM)

This folder contains the **Flask-based model service** used by the Spring Boot backend to perform real-time F10.7 solar flux predictions.  
The service loads a serialized **LightGBM** model (`model.pkl`) exported with **Joblib** and exposes it through a REST API endpoint (`/predict`).

---

## Project Structure
```
model-service/
│
├── model_service.py          # Flask application serving the model
├── model.pkl   # Serialized LightGBM model (via Joblib)
├── requirements.txt          # Python dependencies
├── Dockerfile                # Container build for model service
└── README.md                 # This file
```

---

## Features
- Loads and serves the trained LightGBM model for inference.  
- Exposes a `/predict` POST endpoint that accepts JSON input and returns a predicted F10.7 value.  
- Integrated with **Docker Compose** for containerized deployment.  
- Communicates with the **Spring Boot backend** via HTTP (JSON).  

---

## How It Works
1. The Flask app loads `model.pkl` using **Joblib** when the container starts.  
2. The `/predict` endpoint receives feature data (JSON) from the Spring Boot backend.  
3. The model performs inference and returns the predicted flux value as JSON.  

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

### 4. Run the Flask app
```bash
python model_service.py
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
- Build the Flask model image (`spring-boot-model-service`)  
- Start the container (`flask-model`)  
- Expose the service at `http://localhost:5000/predict`

---

## Example Request
Example POST request to the `/predict` endpoint:
```bash
curl -X POST -H "Content-Type: application/json" \
-d '{
  "features": {
    "lag1":150,"lag2":149,"lag3":148,"lag4":147,"lag5":146,"lag6":145,
    "lag7":144,"lag8":143,"lag9":142,"lag10":141,"lag11":140,"lag12":139,
    "lag13":138,"lag14":137,"lag15":136,"lag16":135,"lag17":134,"lag18":133,
    "lag19":132,"lag20":131,"lag21":130,"lag22":129,"lag23":128,"lag24":127,
    "lag25":126,"lag26":125,"lag27":124,
    "ap_mean":15,"ap_max":30,
    "ap_mean_lag1":14,"ap_mean_lag2":14,"ap_mean_lag3":13,
    "ap_max_lag1":28,"ap_max_lag2":29,"ap_max_lag3":31
  }
}' \
http://localhost:5000/predict
```

```bash
Invoke-WebRequest -Uri "http://localhost:8080/api/predictions/run" -Method POST `
-Headers @{ "Content-Type" = "application/json" } `
-Body '{
  "features": {
    "lag1":150,"lag2":149,"lag3":148,"lag4":147,"lag5":146,"lag6":145,"lag7":144,
    "lag8":143,"lag9":142,"lag10":141,"lag11":140,"lag12":139,"lag13":138,
    "lag14":137,"lag15":136,"lag16":135,"lag17":134,"lag18":133,"lag19":132,
    "lag20":131,"lag21":130,"lag22":129,"lag23":128,"lag24":127,"lag25":126,
    "lag26":125,"lag27":124,
    "ap_mean":15,"ap_max":30,
    "ap_mean_lag1":14,"ap_mean_lag2":14,"ap_mean_lag3":13,
    "ap_max_lag1":28,"ap_max_lag2":29,"ap_max_lag3":31
  }
}'
```

Response:
```json
{"prediction": 133.79525854891173}
```

---

## Integration Notes
- The Spring Boot backend calls this service at:
  ```
  http://localhost:5000/predict
  ```
  (or `http://model-service:5000/predict` (*I just used localhost*) when both run inside Docker Compose)
- Ensure that the Flask and PostgreSQL containers are running before backend startup.

---

## Dependencies
- Python 3.12+
- Flask  
- Joblib  
- LightGBM  
- NumPy  

---

## Credits
Developed as part of the **CSULA Aerospace Senior Design (Fall 2025 - Spring 2026)** project for F10.7 solar flux forecasting.  
