package dev.laubfrosch.archery.backend.scoring;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/scores")
@Tag(name = "Score")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScoreResource extends GenericResource<Score, String> {

    @Inject
    ScoreRepository repository;

    @Override
    protected PanacheRepositoryBase<Score, String> getRepository() {
        return repository;
    }
}
