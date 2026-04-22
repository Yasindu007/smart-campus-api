# Smart Campus REST API
WKYM DE SILVA - W2120431 - 20232969

## API Overview
This project implements a versioned Smart Campus REST API using Java, JAX-RS (Jersey), Maven, and in-memory collections (`HashMap` and `ArrayList`) only. The API models a campus monitoring system where rooms can be created, listed, retrieved, and safely removed, while sensors are assigned to rooms and can record historical readings.

The API follows a resource-oriented design:
- `GET /api/v1` is a discovery endpoint that returns API metadata, version information, administrative contact details, and links to the main resource collections.
- `/api/v1/rooms` manages campus rooms. Clients can create rooms, list all rooms, fetch a specific room by ID, and delete a room only when it has no sensors assigned.
- `/api/v1/sensors` manages sensors linked to existing rooms. Clients can create sensors, list all sensors, and filter sensors by type using a query parameter such as `?type=CO2`.
- `/api/v1/sensors/{sensorId}/readings` is a nested sub-resource for sensor reading history. Clients can fetch previous readings or append a new reading for a sensor. When a new reading is added, the parent sensor's `currentValue` is updated.

The implementation uses JSON request and response bodies, meaningful HTTP status codes, custom exception mappers for error responses, and a JAX-RS logging filter for request and response logging.

Base URL: `http://localhost:8080/smart-campus-api/api/v1`

## Build and Run
### NetBeans
1. Install Java 11+, Apache NetBeans, and Apache Tomcat.
2. Open NetBeans and choose `File > Open Project`.
3. Select the `smart-campus-api` project folder.
4. Make sure Tomcat is configured in NetBeans under `Services > Servers`.
5. Right-click the project and select `Clean and Build`.
6. Right-click the project and select `Run`.
7. After deployment, open `http://localhost:8080/smart-campus-api/api/v1`.

### Maven and Tomcat
1. Install Java 11+, Maven, and Apache Tomcat.
2. Run `mvn clean package`.
3. Copy `target/smart-campus-api.war` into the Tomcat `webapps` folder.
4. Start Tomcat.
5. Open `http://localhost:8080/smart-campus-api/api/v1`.

## Sample curl Commands
```bash
curl -i -X GET http://localhost:8080/smart-campus-api/api/v1
curl -i -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/rooms -H "Content-Type: application/json" -d "{\"name\":\"Library West\",\"capacity\":120}"
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors -H "Content-Type: application/json" -d "{\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0,\"roomId\":\"ROOM-1\"}"
curl -i -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/SENSOR-1/readings -H "Content-Type: application/json" -d "{\"value\":445.2}"
curl -i -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/ROOM-1
```

## Conceptual Report Answers

### Part 1.1 - JAX-RS Resource Lifecycle
By default, JAX-RS resource classes are request-scoped, meaning a new resource instance is created for each incoming request. Because of this, shared application data should not be stored only inside resource instance fields. In this project, shared room, sensor, and reading data is stored in a separate static store class using in-memory collections. Synchronized store methods are used to reduce race conditions when multiple requests access or update the same maps and lists.

### Part 1.2 - Why HATEOAS Matters
Hypermedia links help clients discover available API actions from the responses themselves instead of depending only on external static documentation. This supports RESTful design because responses can guide clients to related resources such as rooms, sensors, and readings. It also makes the API easier to evolve, because clients can follow provided links rather than hard-coding every endpoint path.

### Part 2.1 - IDs vs Full Objects in Room Lists
Returning only room IDs reduces response size and saves bandwidth, especially when there are many rooms. However, clients then need extra requests to fetch the full details for each room. Returning full room objects increases payload size, but it reduces follow-up requests and makes client-side rendering simpler because all room data is available immediately.

### Part 2.2 - DELETE Idempotency
DELETE is idempotent because repeating the same delete request does not cause additional state changes after the resource has already been removed. In this implementation, a room can only be deleted if it exists and has no sensors assigned to it. Once it is deleted, repeating the same DELETE request will not delete anything else or create a new state change.

### Part 3.1 - @Consumes(MediaType.APPLICATION_JSON)
The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the POST method accepts JSON request bodies. If a client sends another format, such as `text/plain` or `application/xml`, JAX-RS rejects the request with `415 Unsupported Media Type` before the business logic runs. This prevents invalid payload formats from being processed by the resource method.

### Part 3.2 - QueryParam vs PathParam for Filtering
Query parameters are better for optional filtering and searching collections, such as `/sensors?type=CO2`. The main resource remains `/sensors`, while the query string describes how the collection should be filtered. Path parameters are better for identifying a specific resource, such as `/rooms/ROOM-1`, rather than optional search criteria.

### Part 4.1 - Benefit of the Sub-Resource Locator Pattern
The sub-resource locator pattern separates nested endpoint logic into a dedicated class. In this project, sensor reading operations are handled by `SensorReadingResource` instead of putting every nested path inside `SensorResource`. This keeps each resource class focused, improves readability, and makes the API easier to maintain as nested routes grow.

### Part 5.1 - Why 422 for Missing Linked Resource
HTTP `422 Unprocessable Entity` is appropriate when the request URI is valid and the JSON syntax is acceptable, but the payload contains a semantically invalid reference. For example, creating a sensor with a `roomId` that does not exist is not the same as requesting a missing endpoint. The request target exists, but the submitted data cannot be processed because the linked room is missing.

### Part 5.2 - Risk of Exposing Stack Traces
Exposing internal Java stack traces can reveal package names, class names, method names, file structure, framework versions, and implementation details. Attackers can use this information to identify weak points in the application and plan more targeted attacks. Returning generic JSON error responses is safer because it gives clients useful error information without exposing internal server details.

### Part 5.3 - Why Use Filters for Logging
JAX-RS filters are useful for cross-cutting concerns such as logging because they run automatically around requests and responses. This avoids repeating `Logger.info()` calls inside every resource method. It keeps resource classes focused on business logic and makes logging easier to update consistently across the whole API.
