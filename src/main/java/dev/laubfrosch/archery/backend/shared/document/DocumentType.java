package dev.laubfrosch.archery.backend.shared.document;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Cacheable
@Table(name = "document_types")
public class DocumentType extends PanacheEntityBase {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private DocumentTypeId id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
