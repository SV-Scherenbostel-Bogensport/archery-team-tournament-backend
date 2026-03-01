package dev.laubfrosch.archery.backend.scoring.arrow;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class ArrowRepository  implements PanacheRepositoryBase<Arrow, UUID> {
}
