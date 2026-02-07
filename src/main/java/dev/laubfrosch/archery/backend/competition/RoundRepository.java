package dev.laubfrosch.archery.backend.competition;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class RoundRepository implements PanacheRepositoryBase<Round, UUID> {
}