package dev.laubfrosch.archery.backend.scoring;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TargetFaceRepository implements PanacheRepositoryBase<TargetFace, UUID> {
}
