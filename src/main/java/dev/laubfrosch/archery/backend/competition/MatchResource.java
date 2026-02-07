package dev.laubfrosch.archery.backend.competition;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/matches")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MatchResource extends GenericResource<Match, UUID> {

    @Inject
    MatchRepository repository;

    @Override
    protected PanacheRepositoryBase<Match, UUID> getRepository() {
        return repository;
    }
}