### pingu-backend — How to run and use

This Spring Boot module exposes a secured REST API for exchange rates loaded from the `infrastructure` module’s JSONs and optional seeded demo data. It also provides an OpenAPI/Swagger UI.

#### Quick start
1. Build (optional):
   ```bash
   mvn -q install
   ```
2. Run the backend only:
   ```bash
   mvn -pl backend spring-boot:run
   ```
3. Open Swagger UI:
   - UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Default port is `8080` (configurable via `server.port`).

#### Authentication (JWT)
- Scheme: Bearer token in the `Authorization` header.
- Demo users (in-memory):
  - username: `admin`, password: `password`
  - username: `user`,  password: `password`
- Obtain token:
  - Endpoint: `POST /auth/token`
  - Body:
    ```json
    { "username": "admin", "password": "password" }
    ```
  - Response:
    ```json
    { "token": "<JWT>", "tokenType": "Bearer" }
    ```
- Use token in requests: header `Authorization: Bearer <JWT>`

Example with curl (PowerShell escaping may vary):
```bash
curl -s -X POST http://localhost:8080/auth/token ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"password\"}"
```

Then call a protected endpoint:
```bash
curl -H "Authorization: Bearer <JWT>" http://localhost:8080/api/rates/active
```

In Swagger UI, click the “Authorize” button and enter: `Bearer <JWT>`.

#### Endpoints (secured)
- `GET /api/rates` — list all rate versions
- `GET /api/rates/active` — active rate version
- `GET /api/rates/{id}` — version by id
- `GET /api/rates/{id}/convert?from=CHF&to=USD&amount=100` — convert amount
- `POST /api/rates` — create/update a version (in-memory only, for demo)

#### Seeded demo data
On startup, an in-memory seeder may create extra historical versions based on the JSON data shape.

Defaults (if not overridden):
- `seed.enabled=true` — seeding is on
- `seed.count=3` — create 3 past daily versions
- `seed.baseDate=<today>` — seed days before this date
- `seed.uploadedBy=admin`
- `seed.activeLatest=false` — keep the JSON’s active version active

Each seeded entry:
- `id`: `seed-version-<yyyyMMdd>`
- `versionName`: `<yyyy-MM-dd> Daily Rates`
- `baseCurrency`: copied from existing data (defaults to CHF)
- `rates`: copied and varied slightly (+/− ~1%)
- `uploadedAt`: midnight of that date
- `uploadedBy`: as configured
- `active`: false (unless `seed.activeLatest=true`)

Configure in `backend/src/main/resources/application.properties` or environment variables:
```properties
# Seeding
seed.enabled=true
seed.count=3
seed.baseDate=2026-03-08
seed.uploadedBy=admin
seed.activeLatest=false
```

#### Configuration reference
```properties
# Server
server.port=8080

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html

# JWT (demo secrets — replace for real use)
security.jwt.secret=ZmFrZV9waW5ndV9zZWNyZXRfMzJieXRlc19iYXNlNjQ=  # base64 HS256 key
security.jwt.ttlSeconds=3600

# Seeding — see section above
seed.enabled=true
seed.count=3
seed.baseDate=
seed.uploadedBy=admin
seed.activeLatest=false
```

#### Frontend integration
See `../docs/FRONTEND_INTEGRATION.md` for step-by-step guidance, example calls, and required headers.

#### Notes
- The service keeps data in memory only. `POST /api/rates` and seeded entries are lost on restart.
- CORS is permissive for local development; narrow it in `SecurityConfig.corsConfigurationSource()` for production.
