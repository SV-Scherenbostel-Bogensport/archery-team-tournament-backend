package dev.laubfrosch.archery.backend.tournament.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class TeamNameConflictExceptionMapper implements ExceptionMapper<TeamNameConflictException> {

    @Override
    public Response toResponse(TeamNameConflictException e) {
        return Response
                .status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("message", e.getMessage()))
                .build();
    }
}