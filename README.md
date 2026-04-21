# Smart Campus REST API

## API Overview
This project implements the Smart Campus API using Java, JAX-RS (Jersey), Maven, and in-memory collections (`HashMap` and `ArrayList`) only.

Base URL: `http://localhost:8080/smart-campus-api/api/v1`

## Build and Run
1. Install Java 11+ and Maven.
2. Run `mvn clean package`.
3. Deploy `target/smart-campus-api.war` to a servlet container (for example Tomcat).
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
