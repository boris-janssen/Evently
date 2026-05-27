# Evently

Evently is a backend REST API for building event sign-up forms and collecting responses. You define a form with typed fields, accept submissions that are validated against the form definition, and retrieve the collected entries.

Built with **Java 21**, **Spring Boot 3.3**, and **MongoDB**.

---

## Running the app

Requires [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/).

```bash
docker compose up --build
```

The API is available at `http://localhost:8080`.

### Stopping the app

```bash
docker compose down        # stop and remove containers
docker compose down -v     # also delete the stored data
```

### Environment variables

| Variable      | Default                             | Description            |
|---------------|-------------------------------------|------------------------|
| `MONGODB_URI` | `mongodb://localhost:27017/evently` | MongoDB connection URI |
| `SERVER_PORT` | `8080`                              | Application port       |

---

## Technical details

- **Field types** — each field in a form must declare one of: `TEXT`, `EMAIL`, `NUMBER`, `DATE`, `PHONE`, `BOOLEAN`
- **Validation** — on submission, all answers are validated against the form definition: required fields must be present, values must match their declared type, and unknown fields are rejected
- **Non-fail-fast** — all validation errors are collected and returned together in a single response
- **Date format** — `DATE` fields expect ISO 8601 format (`YYYY-MM-DD`)
- **Persistence** — data is stored in MongoDB; when running via Docker Compose a named volume (`mongodb_data`) keeps data across container restarts

---

## API endpoints

### Create a form

```
POST /forms
```

```json
{
  "title": "Tech Conference 2026",
  "description": "Annual developer conference sign-up",
  "fields": [
    { "name": "name",  "label": "Full Name",    "type": "TEXT",    "required": true  },
    { "name": "email", "label": "Email Address", "type": "EMAIL",   "required": true  },
    { "name": "age",   "label": "Age",           "type": "NUMBER",  "required": false }
  ]
}
```

Response `201 Created`:

```json
{
  "id": "abc123",
  "title": "Tech Conference 2026",
  "description": "Annual developer conference sign-up",
  "fields": [ ... ],
  "createdAt": "2026-05-27T10:00:00"
}
```

---

### Get a form

```
GET /forms/{id}
```

Response `200 OK` — returns the form definition. Returns `404` if the id does not exist.

---

### Submit an entry

```
POST /forms/{id}/entries
```

```json
{
  "answers": {
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "age": 30
  }
}
```

Response `201 Created`:

```json
{
  "id": "entry456",
  "formId": "abc123",
  "answers": {
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "age": 30
  },
  "submittedAt": "2026-05-27T10:05:00"
}
```

Returns `400` with all validation errors when the submission is invalid:

```json
{
  "errors": [
    { "field": "email",   "message": "Invalid email format" },
    { "field": "name",    "message": "Field 'name' is required" },
    { "field": "unknown", "message": "Unknown field: 'unknown'" }
  ]
}
```

---

### Get all entries for a form

```
GET /forms/{id}/entries
```

Response `200 OK`:

```json
[
  {
    "id": "entry456",
    "formId": "abc123",
    "answers": { "name": "Alice Johnson", "email": "alice@example.com", "age": 30 },
    "submittedAt": "2026-05-27T10:05:00"
  }
]
```
