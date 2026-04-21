package com.example.smartcampus.resource;

import com.example.smartcampus.exception.LinkedResourceNotFoundException;
import com.example.smartcampus.exception.RoomNotEmptyException;
import com.example.smartcampus.model.ErrorResponse;
import com.example.smartcampus.model.Room;
import com.example.smartcampus.store.CampusStore;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
public class RoomResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getRooms() {
        return CampusStore.getAllRooms();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(Room room, @Context UriInfo uriInfo) {
        Room created = CampusStore.addRoom(room);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build())
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Room getRoomById(@PathParam("id") int id) {
        Room room = CampusStore.getRoomById(id);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room with id " + id + " was not found.");
        }
        return room;
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") int id) {
        Room room = CampusStore.getRoomById(id);
        if (room == null) {
            ErrorResponse body = new ErrorResponse(404, "Room " + id + " was not found.");
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(body)
                    .build();
        }
        if (!CampusStore.getSensorsByRoomId(id).isEmpty()) {
            throw new RoomNotEmptyException("Room " + id + " cannot be deleted because it has sensors.");
        }
        CampusStore.deleteRoom(id);
        return Response.noContent().build();
    }
}
