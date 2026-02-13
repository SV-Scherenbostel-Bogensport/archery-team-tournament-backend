package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/tournaments")
@Tag(name = "Tournament")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TournamentResource extends GenericResource<Tournament, UUID> {

    @Inject
    TournamentRepository repository;

    @Override
    protected PanacheRepositoryBase<Tournament, UUID> getRepository() {
        return repository;
    }
}
