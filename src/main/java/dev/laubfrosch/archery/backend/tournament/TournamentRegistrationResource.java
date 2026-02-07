package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/tournament-registrations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TournamentRegistrationResource extends GenericResource<TournamentRegistration, UUID> {

    @Inject
    TournamentRegistrationRepository repository;

    @Override
    protected PanacheRepositoryBase<TournamentRegistration, UUID> getRepository() {
        return repository;
    }
}
