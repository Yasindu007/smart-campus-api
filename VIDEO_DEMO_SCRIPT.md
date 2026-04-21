# 10-Minute Video Demo Script (Postman)

## Minute 0-1: Intro
1. Show your face and introduce yourself.
2. State this is the Smart Campus API using Java, JAX-RS, Maven, and in-memory storage.
3. Show Postman environment with `baseUrl = http://localhost:8080/smart-campus-api/api/v1`.

## Minute 1-2: Discovery
1. `GET {{baseUrl}}`
2. Explain `200 OK` and metadata + links.

## Minute 2-4: Rooms
1. `POST {{baseUrl}}/rooms`
2. Header: `Content-Type: application/json`
3. Body:
```json
{"name":"Library West","capacity":120}
```
4. Explain `201 Created` + `Location` header.
5. `GET {{baseUrl}}/rooms`
6. `GET {{baseUrl}}/rooms/ROOM-1`

## Minute 4-6: Sensors and validation/filtering
1. Invalid sensor link test:
   - `POST {{baseUrl}}/sensors`
   - Body:
```json
{"type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"ROOM-999"}
```
   - Explain `422`.
2. Valid sensor create:
   - `POST {{baseUrl}}/sensors`
   - Body:
```json
{"type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"ROOM-1"}
```
   - Explain `201`.
3. `GET {{baseUrl}}/sensors?type=CO2` and explain filtering.

## Minute 6-8: Sub-resource readings
1. `GET {{baseUrl}}/sensors/SENSOR-1/readings`
2. `POST {{baseUrl}}/sensors/SENSOR-1/readings`
3. Header: `Content-Type: application/json`
4. Body:
```json
{"value":445.2}
```
5. `GET {{baseUrl}}/sensors` and show updated `currentValue`.

## Minute 8-10: Error handling
1. 409 test: `DELETE {{baseUrl}}/rooms/ROOM-1` while it still has sensors.
2. 403 test:
   - Create maintenance sensor:
```json
{"type":"CO2","status":"MAINTENANCE","currentValue":0,"roomId":"ROOM-1"}
```
   - Then post reading to that sensor and explain `403`.
3. 500 test: trigger a controlled unexpected error and show generic JSON response (no stack trace).
