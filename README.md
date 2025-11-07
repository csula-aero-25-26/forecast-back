# (forecast-back): CSULA Aerospace Senior Design — F10.7 Solar Flux Forecasting System

## High-Level Overview

This repository contains a **containerized forecasting system** that predicts daily solar radio flux (F10.7 cm) using machine learning.  
It is composed of multiple microservices connected through a **Spring Boot backend**, with data and models stored in **PostgreSQL**.

---

## Architecture Summary

```
Client / Frontend
        │
        ▼
Spring Boot Backend  (Java 21)
 ├── Calls → Fetch Service  (/latest)
 └── Calls → Model Service  (/predict/{model}/{horizon})
        │
        ▼
PostgreSQL Database  (model registry, features, predictions)
```

---

## ⚙️ Core Services

| Service                 | Tech Stack            | Port                           | Role                                                   |
| ----------------------- | --------------------- | ------------------------------ | ------------------------------------------------------ |
| **Spring Boot Backend** | Java 21 / Maven       | 8080                           | Main orchestrator between frontend and microservices   |
| **Model Service**       | Python 3.12 / FastAPI | 5000                           | Loads LightGBM model and returns predictions           |
| **Fetch Service**       | Python 3.12 / FastAPI | 5500                           | Downloads, processes, and prepares solar data features |
| **PostgreSQL**          | 15+                   | 5332 (host) / 5432 (container) | Stores models, metadata, and predictions               |

All services are containerized and run together with **Docker Compose**.

---

## Data Flow Overview

1. **Fetch Service** downloads the latest input dataset and generates features (_based on model .py file_).
2. **Model Service** retrieves feature requirements from Postgres, loads the model, and predicts the next F10.7 flux value.
3. **Spring Boot Backend** coordinates the process and exposes `/api/predictions/run` to clients.
4. **PostgreSQL** stores models, features, predictions, and ground truths.

---

## Running the Full Stack with Docker

To start everything (backend, database, and model service) in containers:

```bash
docker compose up --build
```

If you’re coding in IntelliJ and want live debugging:

1. Run only the database and model service in Docker:

```bash
docker compose up db model-service flask-service
```

2. In IntelliJ → Run Configuration → Active Profiles:

```
dev
```

3. The backend will connect automatically to:

- Postgres: `localhost:5332`
- Model Service: `localhost:5000`
- Fetch Service: `localhost:5500`

For integration testing or final demos, switch back to Docker Compose.

---

## Accessing the Database

Inside Intellij IDEA, be sure to open the project with the 'spring-boot' file instead of 'backend'

To access the database, go to the IntelliJ terminal and enter the following commands (make sure Docker is running in the background):

> docker exec -it postgres-spring-boot bash

> psql -U aspteam -d forecastdb

From there, you can see/access the tables & contents using basic SQL commands.
