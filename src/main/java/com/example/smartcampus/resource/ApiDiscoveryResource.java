package com.example.smartcampus.resource;

import com.example.smartcampus.model.ApiRootResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ApiDiscoveryResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApiRootResponse getApiRoot() {
        ApiRootResponse response = new ApiRootResponse("Smart Campus API", "v1", "smart-campus-support@university.edu");
        response.getLinks().put("rooms", "/api/v1/rooms");
        response.getLinks().put("sensors", "/api/v1/sensors");
        response.getLinks().put("sensorReadings", "/api/v1/sensors/{sensorId}/readings");
        return response;
    }
}
