package dev.laubfrosch.archery.backend.scoring;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/sets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SetResource extends GenericResource<Set, UUID> {

    @Inject
    SetRepository repository;

    @Override
    protected PanacheRepositoryBase<Set, UUID> getRepository() {
        return repository;
    }
}
