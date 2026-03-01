package dev.laubfrosch.archery.backend.scoring.target;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TargetRepository implements PanacheRepositoryBase<Target, UUID> {
}
