package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatusResource extends GenericResource<Status, Integer> {

    @Inject
    StatusRepository repository;

    @Override
    protected PanacheRepositoryBase<Status, Integer> getRepository() {
        return repository;
    }
}
