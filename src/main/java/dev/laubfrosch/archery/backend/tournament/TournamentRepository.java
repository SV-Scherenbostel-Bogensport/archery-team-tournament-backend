package dev.laubfrosch.archery.backend.tournament;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TournamentRepository implements PanacheRepositoryBase<Tournament, UUID> {

    public Optional<Tournament> findByIdWithStagesRoundsAndMatches(UUID id) {
        return find("""
                FROM Tournament t
                LEFT JOIN FETCH t.stages s
                LEFT JOIN FETCH s.rounds r
                LEFT JOIN FETCH r.matches
                WHERE t.id = :id
                """, Parameters.with("id", id))
                .firstResultOptional();
    }
}
