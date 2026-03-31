IntelliJ shared run configs for this repo.

Files in this folder are picked up automatically by IntelliJ IDEA as shared project run configurations.

Configs included:

- `TB - Install DB (monolith demo)` initializes PostgreSQL schema and demo data.
- `TB - Monolith Local` runs the full backend in single-process mode.
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
- For debugging, click `Debug` on the same configuration. IntelliJ will attach to the Maven-started JVM automatically.
- Default local database credentials are `postgres/postgres` with database `thingsboard`.
- Monolith mode uses in-memory queue and caffeine cache.
- Microservices mode uses Kafka, ZooKeeper, and Redis-compatible cache.
