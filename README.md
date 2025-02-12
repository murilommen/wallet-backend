

## Local development

You will need Java >= 21 and Docker Compose locally in order to run this project as intended.

### Start the database locally with Docker Compose
`docker compose up -d`
In case you already have a local PostgreSQL instance running on 5432, then this step becomes optional
### Build the app and start the server
`./gradlew run`

And access the swagger documentation for the API operations at http://localhost:8080/swagger-ui

>**NOTE:** If you already have something setup at ports 5432 (Postgres) or 8080 (the server), you can change them
> on `docker-compose.yaml` and `src/main/resources/application.properties` respectively

# To-do's
- [ ] Link the database with environment variables
- [ ] Make sure functional requirements are met
- [ ] Create unit tests
- [ ] Make the app start with Docker Compose
---
# What I'd do next
- [ ] Enable a CI/CD pipeline to test, build and deploy the app to a desired infrastructure
