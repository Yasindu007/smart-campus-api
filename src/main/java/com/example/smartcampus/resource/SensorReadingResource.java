package com.example.smartcampus.resource;

import com.example.smartcampus.exception.LinkedResourceNotFoundException;
import com.example.smartcampus.exception.SensorUnavailableException;
import com.example.smartcampus.model.Sensor;
import com.example.smartcampus.model.SensorReading;
import com.example.smartcampus.store.CampusStore;
import java.time.Instant;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SensorReadingResource {
    private final int sensorId;

    public SensorReadingResource(int sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        Sensor sensor = CampusStore.getSensorById(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor with id " + sensorId + " was not found.");
        }
        return CampusStore.getReadingsBySensorId(sensorId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = CampusStore.getSensorById(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor with id " + sensorId + " was not found.");
        }
        if (!sensor.isAvailable()) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is currently unavailable.");
        }
        if (reading.getTimestamp() == null || reading.getTimestamp().isEmpty()) {
            reading.setTimestamp(Instant.now().toString());
        }
        SensorReading created = CampusStore.addReading(sensorId, reading);
        sensor.setCurrentValue(created.getValue());
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
}
