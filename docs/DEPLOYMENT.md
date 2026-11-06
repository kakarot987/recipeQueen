# Production deployment guide

This is a **staging/production preparation guide**, not a production security certification.

## Prerequisites and infrastructure

- A TLS-enabled reverse proxy or managed load balancer with a valid certificate, pointing to `127.0.0.1:8080`. **Never expose the port-8080 HTTP service directly to the Internet.**
- Docker Engine and Compose, Java 11 + Gradle for the backend JAR, or an equivalent CI build pipeline.
- Restrict the MySQL database to the Docker internal network. Do not forward port 3306 publicly.
- Before adopting Flyway on an existing database, **back it up**, test migrations on a restored staging copy, and inspect legacy recipe join-table names. Fresh installs are covered by V1, legacy additions by V2.
- Rotate any previously committed JWT keys/database passwords before deploying.

## Secrets

From the repository root, create an untracked `.env` file containing:
```
DB_PASSWORD=<use-your-own-strong-random-secret>
MYSQL_ROOT_PASSWORD=<use-a-different-strong-random-secret>
JWT_SECRET=<use-a-long-random-jwt-hmac-secret>
PUBLIC_ORIGIN=https://recipes.example.com
```
Replace every angle-bracket placeholder. Never commit this file.

For managed deployments, prefer a secret manager; avoid putting secrets into build arguments, Docker image layers, source control, or browser JavaScript.

## Build and deploy

```bash
cd QueensKitchenSpringBoot
./gradlew clean test bootJar
cd ..
docker compose -f docker-compose.prod.yml up --build -d
docker compose -f docker-compose.prod.yml ps
curl -fsS http://127.0.0.1:8080/actuator/health
```

Place an HTTPS reverse proxy in front of localhost:8080; the Angular UI uses relative `/api` URLs so no environment-specific frontend rebuild is required. The Nginx configuration applies basic authentication-path rate limiting. For multiple replicas, use a shared gateway/WAF rate limiter and centralized sessions/metrics.

## Monitoring and operations

- Expose Spring Actuator `/actuator/health` to your internal monitoring service; do not expose other management endpoints publicly.
- Collect structured request status and application error logs in a centralized platform. Avoid storing tokens/passwords.
- Monitor availability, 5xx rates, MySQL storage usage, database backups, and CPU/memory pressure.
- Enable HTTPS, HSTS at the TLS edge, secure cipher suites, and routine dependency scans.
- Back up the MySQL Docker volume daily and **test restoring** into staging. Keep backups encrypted and access-controlled.
- Apply migrations to staging first, then deploy a reviewed release. `ddl-auto=validate` ensures Hibernate does not silently rewrite the schema.

## Rollback

Keep the previous API and web image tags and the corresponding Flyway schema release. A rollback that changes the schema needs a reviewed restore/migration plan; simply rolling back containers is **not** sufficient for incompatible migrations.

## Remaining security modernization

The original Angular 13 and Spring Boot 2.5 dependencies are legacy and need controlled framework upgrades. The local `.npmrc` has a temporary peer-dependency compatibility override. Upgrade both framework stacks on separate branches and rerun CI plus E2E before exposing the app publicly.
