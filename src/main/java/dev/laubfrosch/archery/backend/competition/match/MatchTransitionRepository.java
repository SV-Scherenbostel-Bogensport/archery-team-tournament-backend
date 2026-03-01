package dev.laubfrosch.archery.backend.competition.match;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class MatchTransitionRepository implements PanacheRepositoryBase<MatchTransition, UUID> {
}
