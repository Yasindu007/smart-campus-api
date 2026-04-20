# Smart Campus REST API Coursework Report

## Project Overview
This coursework implements a Smart Campus REST API using Java, JAX-RS (Jersey), Maven, and in-memory storage (`HashMap`/`ArrayList`).

Base URL:
- `http://localhost:8080/smart-campus-api/api/v1`

## Part 1.1 - Architecture and Configuration
The project uses a JAX-RS `Application` subclass with `@ApplicationPath("/api/v1")` to expose all endpoints under a versioned API root.

By default, resource classes are request-scoped in JAX-RS, so each request receives a new resource instance. Because the data store is static and shared (`CampusStore`), concurrent requests can access the same maps/lists at the same time. To protect in-memory consistency, synchronized static methods are used in `CampusStore` for read/write operations that touch shared collections and ID counters.

## Part 1.2 - Discovery Endpoint and HATEOAS
`GET /api/v1` returns JSON metadata with API name, version, support contact, and resource links.

HATEOAS is useful because clients can discover valid next routes directly from server responses instead of hard-coding paths in multiple places. This makes the API easier to evolve and reduces dependency on external static documentation.

## Part 2.1 - Room Implementation
Implemented room endpoints:
- `GET /rooms` (list)
- `POST /rooms` (create, returns `201 Created`)
- `GET /rooms/{id}` (detail)

`POST /rooms` returns both response body and `Location` header for the created resource.

Returning full room objects is more verbose than returning IDs only, but it reduces follow-up calls for clients and simplifies client-side data binding. Returning only IDs is lighter on bandwidth but shifts more work to the client.

## Part 2.2 - Room Deletion and Business Logic
`DELETE /rooms/{id}` is implemented with a business rule: room deletion is blocked when the room still has linked sensors, returning `409 Conflict`.

DELETE is idempotent in intent: repeated DELETE calls do not recreate deleted state. In this API, a first successful delete returns `204`, and later calls for the same ID return the mapped not-found behavior (`422`) because the room no longer exists.

## Part 3.1 - Sensor Integrity and Media Type
Implemented:
- `POST /sensors` with `roomId` existence validation

If `roomId` does not exist, the API throws `LinkedResourceNotFoundException` and returns `422`.

`@Consumes(MediaType.APPLICATION_JSON)` enforces JSON payloads. If a client sends an unsupported content type, JAX-RS responds with `415 Unsupported Media Type` before business logic executes.

## Part 3.2 - Filtered Retrieval
Implemented:
- `GET /sensors`
- `GET /sensors?type=CO2`

`@QueryParam("type")` is used because filtering narrows a collection query. `PathParam` is better for identifying a single concrete resource path segment, while query parameters are more suitable for optional search/filter conditions.

## Part 4.1 - Sub-Resource Locator Pattern
Sub-resource locator is implemented in `SensorResource`:
- `/sensors/{sensorId}/readings` delegates to a separate `SensorReadingResource` class.

This keeps routing concerns in the parent resource and reading-specific logic in a focused class, which improves maintainability as nested functionality grows.

## Part 4.2 - Reading History Management
Implemented in `SensorReadingResource`:
- `GET /sensors/{sensorId}/readings`
- `POST /sensors/{sensorId}/readings`

When a reading is posted, the parent sensor’s `currentValue` is updated immediately so historical and latest-value views stay consistent.

## Part 5.1 - Specific Exception Mapping
Implemented mappers:
- `RoomNotEmptyException` -> `409 Conflict`
- `LinkedResourceNotFoundException` -> `422 Unprocessable Entity`
- `SensorUnavailableException` -> `403 Forbidden`

All return structured JSON error bodies with `status` and `message`.

`422` is used for linked-resource payload reference issues (for example, referring to a room ID that is syntactically valid but semantically invalid for processing). This is more precise than `404`, which typically represents direct URI target non-existence.

## Part 5.2 - Global Safety Net
A global `ExceptionMapper<Throwable>` is implemented to return a clean `500` JSON response without stack traces in the client payload.

This prevents technical information disclosure. Raw traces can reveal internal package structure, class names, code paths, library signatures, and operational behavior that can assist targeted attacks.

## Logging
`ApiLoggingFilter` implements:
- `ContainerRequestFilter` for method and URI
- `ContainerResponseFilter` for response status

Logging uses `java.util.logging` only.

## Build and Run
1. `mvn clean package`
2. Deploy `target/smart-campus-api.war` to Tomcat/GlassFish

## curl Test Commands
1.
```bash
curl -i -X GET http://localhost:8080/smart-campus-api/api/v1
```

2.
```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/rooms -H "Content-Type: application/json" -d "{\"name\":\"Engineering-Lab\"}"
```

3.
```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors -H "Content-Type: application/json" -d "{\"type\":\"CO2\",\"roomId\":999,\"currentValue\":0,\"available\":true}"
```

4.
```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors -H "Content-Type: application/json" -d "{\"type\":\"CO2\",\"roomId\":1,\"currentValue\":0,\"available\":true}"
```

5.
```bash
curl -i -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

6.
```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/1/readings -H "Content-Type: application/json" -d "{\"value\":445.2}"
```

7.
```bash
curl -i -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/1
```

## 10-Minute Video Demo Script
1. Show project setup and `@ApplicationPath("/api/v1")`.
2. Run `GET /api/v1` and explain metadata/links.
3. Run room create request and point out `201` + `Location` header.
4. Run `GET /rooms/{id}` for created room.
5. Run sensor POST with invalid `roomId` and explain `422`.
6. Run sensor POST with valid `roomId`.
7. Run `GET /sensors` and `GET /sensors?type=CO2`.
8. Navigate `/sensors/{id}/readings` and run `GET`.
9. Run `POST` reading and explain `currentValue` side effect.
10. Trigger `409`, `403`, and a `500` scenario and show clean JSON errors plus logs.

## Final Checklist Against Rubric

### Covered
- [x] JAX-RS Maven setup with versioned API root
- [x] Discovery endpoint with metadata and resource links
- [x] Room CRUD subset required by spec + business delete constraint
- [x] Room create returns `201` with `Location` header
- [x] Sensor creation validates linked room
- [x] Sensor query filtering via `@QueryParam`
- [x] Sub-resource locator with separate resource class
- [x] Reading POST updates sensor `currentValue`
- [x] Specific exception mapping: `409`, `422`, `403`
- [x] Global `ExceptionMapper<Throwable>` with sanitized `500`
- [x] Request/response logging filter with `java.util.logging`
- [x] Report answers included for all rubric theory prompts
