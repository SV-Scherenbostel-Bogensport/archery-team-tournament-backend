package dev.laubfrosch.archery.backend.competition.match;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class MatchRepository implements PanacheRepositoryBase<Match, UUID> {

    public List<Match> findByRoundId(UUID roundId) {
        return find("round.id = :roundId",
                Parameters.with("roundId", roundId))
                .list();
    }
}
