package dev.laubfrosch.archery.backend.shared.document;

import dev.laubfrosch.archery.backend.shared.ReadOnlyGenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/document-type")
@Tag(name = "Document Type")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentTypeResource extends ReadOnlyGenericResource<DocumentType, DocumentTypeId> {

    @Inject
    DocumentTypeRepository repository;

    @Override
    protected PanacheRepositoryBase<DocumentType, DocumentTypeId> getRepository() {
        return repository;
    }
}
