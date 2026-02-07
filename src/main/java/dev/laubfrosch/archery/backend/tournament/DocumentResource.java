package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentResource extends GenericResource<Document, UUID> {

    @Inject
    DocumentRepository repository;

    @Override
    protected PanacheRepositoryBase<Document, UUID> getRepository() {
        return repository;
    }
}
