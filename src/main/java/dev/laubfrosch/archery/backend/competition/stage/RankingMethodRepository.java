package dev.laubfrosch.archery.backend.competition.stage;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RankingMethodRepository implements PanacheRepositoryBase<RankingMethod, RankingMethodId> {
}