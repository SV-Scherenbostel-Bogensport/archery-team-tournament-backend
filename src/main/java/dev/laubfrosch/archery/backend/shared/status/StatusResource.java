package dev.laubfrosch.archery.backend.shared.status;

import dev.laubfrosch.archery.backend.shared.ReadOnlyGenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/status")
@Tag(name = "Status")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatusResource extends ReadOnlyGenericResource<Status, StatusId> {

    @Inject
    StatusRepository repository;

    @Override
    protected PanacheRepositoryBase<Status, StatusId> getRepository() {
        return repository;
    }
}