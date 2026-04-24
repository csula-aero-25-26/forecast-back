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
Spring Boot Backend (Java 21)
 ├── Calls → Fetch Service  (/latest)
 ├── Caches → features_daily (PostgreSQL)
 └── Calls → Model Service  (/predict/{model_id})
        │
        ▼
PostgreSQL Database  (model registry, features, predictions)
```

---

## Core Services

| Service                 | Tech Stack            | Port                           | Role                                                    |
| ----------------------- | --------------------- | ------------------------------ | ------------------------------------------------------- |
| **Spring Boot Backend** | Java 21 / Maven       | 8080                           | Orchestrates fetch, caching, inference, and persistence |
| **Model Service**       | Python 3.12 / FastAPI | 5000                           | Loads serialized model artifacts and performs inference |
| **Fetch Service**       | Python 3.12 / FastAPI | 5500                           | Generates canonical feature superset from GFZ data      |
| **PostgreSQL**          | 15+                   | 5332 (host) / 5432 (container) | Stores models, cached features, predictions             |

All services are containerized and run together with **Docker Compose**.

---

## Data Flow Overview

1. The Spring Boot backend receives a prediction request.
2. The backend checks if features for the current date exist in `features_daily`.
3. If not cached:
   - Backend calls Fetch Service (`/latest`)
   - Stores canonical feature superset in PostgreSQL.
4. Backend sends features to Model Service (`/predict/{model_id}`).
5. Model Service:
   - Loads `{model_id}.pkl`
   - Uses its internally stored feature order
   - Performs inference.
6. Backend stores prediction results in `predictions` table.

---

## System Properties

- Fetch Service is stateless and model-agnostic.
- Model Service is stateless and does not access the database.
- Backend owns feature caching and prediction persistence.
- All services communicate over Docker bridge network.

---

## Running the Full Stack with Docker

### NOTE: Inside Intellij IDEA, be sure to open the project with the 'spring-boot' file instead of 'backend'

To start everything (backend, database, and model service) in containers:

```bash
docker compose up --build
```

If you’re coding in IntelliJ and want live debugging:

1. Run only the database and ML services in Docker:

```bash
docker compose up db model-service fetch-service --build
```

> _NOTE_: To restart containers, first use
>
> ```bash
> docker compose down -v
> ```

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

To access the database, go to the IntelliJ terminal and enter the following commands (make sure Docker is running in the background):

```bash
docker exec -it postgres-spring-boot bash
```

```bash
psql -U aspteam -d forecastdb
```

From there, you can see/access the tables & contents using basic SQL commands.
