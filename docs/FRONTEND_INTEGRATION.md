Base URL
- Local: `http://localhost:8080`

Swagger/OpenAPI
- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Authentication
- Scheme: JWT via `Authorization` header.
- Obtain token (demo only):
  - POST `http://localhost:8080/auth/token`
  - Body (JSON):
    ```json
    { "username": "admin", "password": "password" }
    ```
  - Response:
    ```json
    { "token": "<JWT>", "tokenType": "Bearer" }
    ```
- Use token in all protected requests:
  - Header: `Authorization: Bearer <JWT>`

Resources and endpoints
- GET `/api/rates` → List all rate versions (from JSON + runtime additions). Secured.
- GET `/api/rates/active` → Get the currently active version. Secured.
- GET `/api/rates/{id}` → Get a specific version by id. Secured.
- GET `/api/rates/{id}/convert?from=CHF&to=USD&amount=100` → Convert currency using a version's rates. Secured.
- POST `/api/rates` → Create/update a version (in-memory only). Secured.

Data model (example)
```json
{
  "id": "rate-version-001",
  "versionName": "2026-02-13 Daily Rates",
  "baseCurrency": "CHF",
  "rates": { "USD": 1.15, "EUR": 1.05, "CHF": 1.0 },
  "uploadedAt": "2026-02-13T00:00",
  "uploadedBy": "admin",
  "active": true
}
```

Frontend changes required
1. Add authentication flow to obtain and store a JWT token.
   - On app start or before the first API call, POST to `/auth/token` with `{ username, password }`.
   - Store `token` (e.g., in memory, Redux store, or localStorage if acceptable). Prefer in-memory for security.
2. Attach the `Authorization` header to all backend requests:
   - `Authorization: Bearer <token>`
3. Use the following endpoints:
   - To show the active rate set: GET `/api/rates/active`.
   - To list all versions: GET `/api/rates`.
   - To view a version: GET `/api/rates/{id}`.
   - To convert an amount: GET `/api/rates/{id}/convert?from={code}&to={code}&amount={number}`.
   - To upload/add a version: POST `/api/rates` with a JSON body matching the model above.
4. Handle 401/403 errors by re-authenticating (fetching a new token) and retrying.


Notes
- The POST `/api/rates` endpoint stores data in-memory only for the current application run; persistence is not implemented.
- The base currency is taken from the version’s `baseCurrency` and used for conversions: amount is normalized to base via `amount / rate[from]` then multiplied by `rate[to]`.
