IntelliJ shared run configs for this repo.

Files in this folder are picked up automatically by IntelliJ IDEA as shared project run configurations.

Configs included:

- `TB - Install DB (monolith demo)` initializes PostgreSQL schema and demo data.
- `TB - Monolith Local` runs the full backend in single-process mode.
- `TB - Monolith Fast (Application)` runs monolith directly from IntelliJ Application config for faster incremental debug.
- `TB - Monolith Fast (Backend Only)` runs monolith directly from IntelliJ Application config and disables serving embedded UI.
- `TB - Core Fast (Application)` runs tb-core directly from IntelliJ Application config for faster incremental debug.
- `TB - Core Fast (Backend Only)` runs tb-core directly from IntelliJ Application config and disables serving embedded UI.
- `TB - Core`
- `TB - Rule Engine`
- `TB - HTTP Transport`
- `TB - MQTT Transport`
- `TB - COAP Transport`
- `TB - LwM2M Transport`
- `TB - SNMP Transport`
- `TB - UI NGX Dev` runs `ui-ngx` frontend dev server from IntelliJ.
- `TB - Monolith Local (No UI Runtime)` runs backend through Maven with the `embedded-ui` runtime dependency disabled.
- `TB - Monolith Package (No UI)` builds the application package with `ui-ngx` removed from runtime dependencies.
- `TB - Core Package (No UI)` builds the application package for core/no-ui validation with `ui-ngx` removed from runtime dependencies.

Recommended usage

1. Monolith local
   - Start PostgreSQL on `localhost:5432`.
   - Run `TB - Install DB (monolith demo)` once.
   - Run or Debug `TB - Monolith Local`.
   - If you do not want backend to serve UI, run `TB - Monolith Fast (Backend Only)` or `TB - Monolith Local (No UI Runtime)`.

2. Microservices local
   - Start PostgreSQL on `localhost:5432`.
   - Start Kafka on `localhost:9092`.
   - Start ZooKeeper on `localhost:2181`.
   - Start Valkey/Redis on `localhost:6379`.
   - Run `TB - Core`.
   - Run `TB - Rule Engine`.
   - Start the transport services you need.

3. Frontend local
   - Ensure `ui-ngx/node_modules` is installed.
   - Run `TB - UI NGX Dev`.
   - Open `http://localhost:4200`.

Notes

- Embedded UI remains enabled by default. You do not need to edit `application/pom.xml` to get normal full UI behavior back.
- The `ui-ngx` runtime dependency is now controlled by Maven profile `embedded-ui`, which is `activeByDefault=true`.
- Backend/no-ui behavior only happens when you explicitly use `-P!embedded-ui` or a `Backend Only`/`No UI` run configuration.
- These configs use Maven `spring-boot:run`, so they work even if IntelliJ module names differ between machines.
- The `Fast (Application)` configs skip Maven reactor startup and are intended for day-to-day backend debugging after the project is already imported and compiled by IntelliJ.
- The `Fast (Application)` configs require IntelliJ to compile the `application` module first. If you see `ClassNotFoundException` for `org.thingsboard.server.ThingsboardServerApplication`, the compiled classes are missing from `application/target/classes`; run `Build Project` or let the config run with `Before launch: Make`.
- For debugging, click `Debug` on the same configuration. IntelliJ will attach to the Maven-started JVM automatically.
- Default local database credentials are `postgres/postgres` with database `thingsboard`.
- Monolith mode uses in-memory queue and caffeine cache.
- Microservices mode uses Kafka, ZooKeeper, and Redis-compatible cache.
- `TB - UI NGX Dev` is the fastest option when you only change frontend code and want hot reload.

Backend-only profile

- If you want backend to stop serving the embedded UI, use Spring profile `backend-only`.
- This repo now includes `application/src/main/resources/thingsboard-backend-only.yml`.
- It disables static resource mappings and disables the SPA forward controller.
- Example VM option for an IntelliJ backend config:
  `-Dspring.profiles.active=backend-only`
- This is useful if you run frontend separately with `TB - UI NGX Dev` or `yarn start`.
- It reduces backend UI serving overhead, but do not expect a dramatic RAM reduction unless you also change packaging/dependencies to exclude `ui-ngx` entirely.
- The `application` module now puts `ui-ngx` runtime dependency behind Maven profile `embedded-ui` (enabled by default).
- To drop `ui-ngx` from backend runtime/package classpath entirely, disable it with `-P!embedded-ui`.
- Shared config `TB - Monolith Local (No UI Runtime)` already does this for you.
- Shared configs `TB - Monolith Package (No UI)` and `TB - Core Package (No UI)` build/package in this mode.
- See `.run/backend-only-modes-guide.html` for a focused explanation of all backend-only/no-ui modes.

Speed reminder

- `TB - Monolith Fast (Application)` is usually the fastest option for normal backend debugging.
- `TB - Monolith Fast (Backend Only)` is the preferred fast option when you run frontend separately and do not want backend to serve embedded UI.
- `TB - Core Fast (Application)` is slower than monolith because it starts with Kafka, ZooKeeper, and Redis-oriented microservices settings.
- `TB - Core Fast (Backend Only)` is the equivalent backend-only fast option for tb-core in microservices mode.
- Preferred order for debug speed is usually:
  1. `TB - Monolith Fast (Application)`
  2. `TB - Monolith Fast (Backend Only)`
  3. `TB - Core Fast (Application)` / `TB - Core Fast (Backend Only)`
  4. Maven-based configs such as `TB - Monolith Local`, `TB - Core`, and `TB - Monolith Local (No UI Runtime)`
- Use `TB - Monolith Fast (Application)` unless you specifically need to debug microservices separation, Kafka queues, partitioning, or transport-to-core behavior in distributed mode.
