package dev.laubfrosch.archery.backend.competition;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class StageRepository implements PanacheRepositoryBase<Stage, UUID> {
}