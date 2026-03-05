package dev.laubfrosch.archery.backend.participant;

import dev.laubfrosch.archery.backend.api.dto.TeamUpdateRequest;
import dev.laubfrosch.archery.backend.api.dto.TeamWithDetailsDto;
import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/teams")
@Tag(name = "Team")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamResource extends GenericResource<Team, UUID> {

    @Inject
    TeamRepository repository;

    @Override
    protected PanacheRepositoryBase<Team, UUID> getRepository() {
        return repository;
    }
}
