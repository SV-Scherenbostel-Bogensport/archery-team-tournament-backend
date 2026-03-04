package dev.laubfrosch.archery.backend.shared.document;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DocumentTypeRepository implements PanacheRepositoryBase<DocumentType, DocumentTypeId> {
}