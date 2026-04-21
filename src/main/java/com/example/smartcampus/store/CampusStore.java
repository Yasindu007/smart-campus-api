package com.example.smartcampus.store;

import com.example.smartcampus.model.Room;
import com.example.smartcampus.model.Sensor;
import com.example.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CampusStore {
    private static final Map<String, Room> ROOMS = new HashMap<>();
    private static final Map<String, Sensor> SENSORS = new HashMap<>();
    private static final Map<String, List<SensorReading>> READINGS = new HashMap<>();
    private static int roomIdCounter = 0;
    private static int sensorIdCounter = 0;
    private static int readingIdCounter = 0;

    static {
        Room r1 = addRoomInternal(new Room(null, "Library Quiet Study", 80));
        Room r2 = addRoomInternal(new Room(null, "Engineering Lab", 40));
        addSensorInternal(new Sensor(null, "CO2", "ACTIVE", 420.0, r1.getId()));
        addSensorInternal(new Sensor(null, "Temperature", "ACTIVE", 21.5, r2.getId()));
    }

    private CampusStore() {
    }

    public static synchronized List<Room> getAllRooms() {
        return new ArrayList<>(ROOMS.values());
    }

    public static synchronized Room getRoomById(String id) {
        return ROOMS.get(id);
    }

    public static synchronized Room addRoom(Room room) {
        return addRoomInternal(room);
    }

    public static synchronized void deleteRoom(String id) {
        ROOMS.remove(id);
    }

    public static synchronized List<Sensor> getAllSensors() {
        return new ArrayList<>(SENSORS.values());
    }

    public static synchronized Sensor getSensorById(String id) {
        return SENSORS.get(id);
    }

    public static synchronized Sensor addSensor(Sensor sensor) {
        return addSensorInternal(sensor);
    }

    public static synchronized List<Sensor> getSensorsByRoomId(String roomId) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor sensor : SENSORS.values()) {
            if (roomId.equals(sensor.getRoomId())) {
                result.add(sensor);
            }
        }
        return result;
    }

    public static synchronized List<SensorReading> getReadingsBySensorId(String sensorId) {
        return new ArrayList<>(READINGS.getOrDefault(sensorId, new ArrayList<SensorReading>()));
    }

    public static synchronized SensorReading addReading(String sensorId, SensorReading reading) {
        readingIdCounter++;
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId("READ-" + readingIdCounter);
        }
        READINGS.computeIfAbsent(sensorId, key -> new ArrayList<>()).add(reading);
        return reading;
    }

    private static Room addRoomInternal(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            roomIdCounter++;
            room.setId("ROOM-" + roomIdCounter);
        }
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<String>());
        }
        ROOMS.put(room.getId(), room);
        return room;
    }

    private static Sensor addSensorInternal(Sensor sensor) {
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            sensorIdCounter++;
            sensor.setId("SENSOR-" + sensorIdCounter);
        }
        SENSORS.put(sensor.getId(), sensor);
        Room room = ROOMS.get(sensor.getRoomId());
        if (room != null) {
            room.getSensorIds().add(sensor.getId());
        }
        return sensor;
    }
}
