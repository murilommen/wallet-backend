# Wallet Service

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

---

## Why this implementation meets the Wallet project criteria
- Micronaut is built to be non-blocking, using Netty as its underlying server infrastructure, all DB calls and I/O operations happen in separate threads
- Micronaut is designed for a fast startup with minimal memory consumption, it fast recovers from crashes and enables minimal downtime
- This microservice implementation makes it easy to scale the app horizontally with added replicas as needed and a load balancer to distribute workloads
- It handles exceptions gracefully, such as the `IllegalArgumentException` that is thrown on `transferFunds`, or the custom ones on `com.wallet.exceptions`.
- Manages migrations with liquibase, making sure that as soon as the application starts, Micronaut picks it up and applies any changes to the source code
- By using HikariCP under the hood, Micronaut also handles the connection to Postgres via a connection Pool, that can be further configured on `application.properties`
- By implementing the Wallet app using the Hexagonal Architecture, it's easier to extend the usages, swap databases and also build unit tests with Mocks
- It stores a `TransactionJpaEntity` for every transaction made in the database, making it easier to trace the exact wallet balances at any given time

## What needs to be considered for production
Time constraint and also scope of this deliverable has made room for some improvements, that could be addressed in a real-world scenario. 
I've considered them not as relevant as what was accomplished in terms of providing a base for a solid and production-ready app.
They are:
- Centralize env variables definition for a production use-case
- Improve unit test coverage and implement e2e tests with a managed database
- Need to pick up a reliable PostgreSQL instance (preferably cloud-based, manage data compaction, tune the cluster size, etc.)
- Implement a monitoring system with APM -> using a monitoring tool such as Prometheus+Grafana, Datadog, New Relic, etc.
- Design an auditing system to track API calls with request/response parameters (async as an annotation on a separate thread, fail-safe)
- Fine tune `@Retryable` operations parameters as needed
- Enable a CI/CD pipeline to test, build and deploy the app to a desired infrastructure