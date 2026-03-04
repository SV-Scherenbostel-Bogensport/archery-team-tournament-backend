package dev.laubfrosch.archery.backend.competition.stage;

import dev.laubfrosch.archery.backend.shared.ReadOnlyGenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/ranking-method")
@Tag(name = "Ranking Method")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RankingMethodResource extends ReadOnlyGenericResource<RankingMethod, RankingMethodId> {

    @Inject
    RankingMethodRepository repository;

    @Override
    protected PanacheRepositoryBase<RankingMethod, RankingMethodId> getRepository() {
        return repository;
    }
}
