package com.example.smartcampus.resource;

import com.example.smartcampus.exception.LinkedResourceNotFoundException;
import com.example.smartcampus.model.Sensor;
import com.example.smartcampus.store.CampusStore;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
public class SensorResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSensor(Sensor sensor) {
        if (CampusStore.getRoomById(sensor.getRoomId()) == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor because room " + sensor.getRoomId() + " does not exist.");
        }
        Sensor created = CampusStore.addSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> all = CampusStore.getAllSensors();
        if (type == null || type.isEmpty()) {
            return all;
        }
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor sensor : all) {
            if (type.equalsIgnoreCase(sensor.getType())) {
                filtered.add(sensor);
            }
        }
        return filtered;
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource sensorReadingResource(@PathParam("sensorId") int sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
