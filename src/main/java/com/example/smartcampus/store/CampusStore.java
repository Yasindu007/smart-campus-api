package com.example.smartcampus.store;

import com.example.smartcampus.model.Room;
import com.example.smartcampus.model.Sensor;
import com.example.smartcampus.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CampusStore {
    private static final Map<Integer, Room> ROOMS = new HashMap<>();
    private static final Map<Integer, Sensor> SENSORS = new HashMap<>();
    private static final Map<Integer, List<SensorReading>> READINGS = new HashMap<>();
    private static int roomIdCounter = 0;
    private static int sensorIdCounter = 0;
    private static int readingIdCounter = 0;

    static {
        Room r1 = addRoomInternal(new Room(0, "Lab-A"));
        Room r2 = addRoomInternal(new Room(0, "Library-1"));

        addSensorInternal(new Sensor(0, "CO2", r1.getId(), 420.0, true));
        addSensorInternal(new Sensor(0, "TEMPERATURE", r2.getId(), 21.5, true));
    }

    private CampusStore() {
    }

    public static synchronized List<Room> getAllRooms() {
        return new ArrayList<>(ROOMS.values());
    }

    public static synchronized Room getRoomById(int id) {
        return ROOMS.get(id);
    }

    public static synchronized Room addRoom(Room room) {
        return addRoomInternal(room);
    }

    public static synchronized void deleteRoom(int id) {
        ROOMS.remove(id);
    }

    public static synchronized List<Sensor> getAllSensors() {
        return new ArrayList<>(SENSORS.values());
    }

    public static synchronized Sensor getSensorById(int id) {
        return SENSORS.get(id);
    }

    public static synchronized Sensor addSensor(Sensor sensor) {
        return addSensorInternal(sensor);
    }

    public static synchronized List<Sensor> getSensorsByRoomId(int roomId) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor sensor : SENSORS.values()) {
            if (sensor.getRoomId() == roomId) {
                result.add(sensor);
            }
        }
        return result;
    }

    public static synchronized List<SensorReading> getReadingsBySensorId(int sensorId) {
        return new ArrayList<>(READINGS.getOrDefault(sensorId, new ArrayList<>()));
    }

    public static synchronized SensorReading addReading(int sensorId, SensorReading reading) {
        readingIdCounter++;
        reading.setId(readingIdCounter);
        reading.setSensorId(sensorId);
        READINGS.computeIfAbsent(sensorId, key -> new ArrayList<>()).add(reading);
        return reading;
    }

    private static Room addRoomInternal(Room room) {
        roomIdCounter++;
        room.setId(roomIdCounter);
        ROOMS.put(room.getId(), room);
        return room;
    }

    private static Sensor addSensorInternal(Sensor sensor) {
        sensorIdCounter++;
        sensor.setId(sensorIdCounter);
        SENSORS.put(sensor.getId(), sensor);
        return sensor;
    }
}
