package dev.laubfrosch.archery.backend.scoring;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/target-faces")
@Tag(name = "Target Face")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TargetFaceResource extends GenericResource<TargetFace, UUID> {

    @Inject
    TargetFaceRepository repository;

    @Override
    protected PanacheRepositoryBase<TargetFace, UUID> getRepository() {
        return repository;
    }
}
