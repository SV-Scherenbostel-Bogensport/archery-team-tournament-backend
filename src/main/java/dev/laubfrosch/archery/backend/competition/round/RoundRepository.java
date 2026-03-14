package dev.laubfrosch.archery.backend.competition.round;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RoundRepository implements PanacheRepositoryBase<Round, UUID> {

    public List<Round> findByStageId(UUID stageId) {
        return find("stage.id = :stageId ORDER BY roundIndex",
                Parameters.with("stageId", stageId))
                .list();
    }
}
