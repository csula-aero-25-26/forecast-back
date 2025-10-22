# forecast-back

Contains the Spring Boot project used for the backend side. Also contains the PostgreSQL database.

Inside Intellij IDEA, be sure to open the project with the 'spring-boot' file instead of 'backend'

To access the database, go to the IntelliJ terminal and enter the following commands (make sure Docker is running in the background):

> docker exec -it postgres-spring-boot bash

> psql -U aspteam -d forecastdb

From there, you can see/access the tables & contents using basic SQL commands.

---

## Running the Full Stack with Docker

To start everything (backend, database, and model service) in containers:

```bash
docker compose up --build
```

If you’re coding in IntelliJ and want live debugging:

1. Run only the database and model service in Docker:
```bash
docker compose up db model-service
```

2. In IntelliJ → Run Configuration → Active Profiles:
```
dev
```

3. The backend will connect automatically to:
- Postgres: localhost:5332
- Flask: localhost:5000

For integration testing or final demos, switch back to Docker Compose.