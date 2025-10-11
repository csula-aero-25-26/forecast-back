# forecast-back
Contains the Spring Boot project used for the backend side. Also contains the PostgreSQL database.

Inside Intellij IDEA, be sure to open the project with the 'spring-boot' file instead of 'backend'

To access the database, go to the IntelliJ terminal and enter the following commands (make sure Docker is running in the background):
> docker exec -it postgres-spring-boot bash

> psql -U aspteam -d forecastdb

From there, you can see/access the tables & contents using basic SQL commands.
