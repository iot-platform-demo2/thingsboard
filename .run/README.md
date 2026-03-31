IntelliJ shared run configs for this repo.

Files in this folder are picked up automatically by IntelliJ IDEA as shared project run configurations.

Configs included:

- `TB - Install DB (monolith demo)` initializes PostgreSQL schema and demo data.
- `TB - Monolith Local` runs the full backend in single-process mode.
- `TB - Monolith Fast (Application)` runs monolith directly from IntelliJ Application config for faster incremental debug.
- `TB - Core Fast (Application)` runs tb-core directly from IntelliJ Application config for faster incremental debug.
- `TB - Core`
- `TB - Rule Engine`
- `TB - HTTP Transport`
- `TB - MQTT Transport`
- `TB - COAP Transport`
- `TB - LwM2M Transport`
- `TB - SNMP Transport`

Recommended usage

1. Monolith local
   - Start PostgreSQL on `localhost:5432`.
   - Run `TB - Install DB (monolith demo)` once.
   - Run or Debug `TB - Monolith Local`.

2. Microservices local
   - Start PostgreSQL on `localhost:5432`.
   - Start Kafka on `localhost:9092`.
   - Start ZooKeeper on `localhost:2181`.
   - Start Valkey/Redis on `localhost:6379`.
   - Run `TB - Core`.
   - Run `TB - Rule Engine`.
   - Start the transport services you need.

Notes

- These configs use Maven `spring-boot:run`, so they work even if IntelliJ module names differ between machines.
- The `Fast (Application)` configs skip Maven reactor startup and are intended for day-to-day backend debugging after the project is already imported and compiled by IntelliJ.
- The `Fast (Application)` configs require IntelliJ to compile the `application` module first. If you see `ClassNotFoundException` for `org.thingsboard.server.ThingsboardServerApplication`, the compiled classes are missing from `application/target/classes`; run `Build Project` or let the config run with `Before launch: Make`.
- For debugging, click `Debug` on the same configuration. IntelliJ will attach to the Maven-started JVM automatically.
- Default local database credentials are `postgres/postgres` with database `thingsboard`.
- Monolith mode uses in-memory queue and caffeine cache.
- Microservices mode uses Kafka, ZooKeeper, and Redis-compatible cache.

Speed reminder

- `TB - Monolith Fast (Application)` is usually the fastest option for normal backend debugging.
- `TB - Core Fast (Application)` is slower than monolith because it starts with Kafka, ZooKeeper, and Redis-oriented microservices settings.
- Preferred order for debug speed is usually:
  1. `TB - Monolith Fast (Application)`
  2. `TB - Core Fast (Application)`
  3. Maven-based configs such as `TB - Monolith Local` and `TB - Core`
- Use `TB - Monolith Fast (Application)` unless you specifically need to debug microservices separation, Kafka queues, partitioning, or transport-to-core behavior in distributed mode.
