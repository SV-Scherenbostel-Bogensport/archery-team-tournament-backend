package dev.laubfrosch.archery.backend.competition.stage;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StageRepository implements PanacheRepositoryBase<Stage, UUID> {

    public List<Stage> findByTournamentId(UUID tournamentId) {
        return find("tournament.id = :tournamentId ORDER BY stageIndex",
                Parameters.with("tournamentId", tournamentId))
                .list();
    }
}
