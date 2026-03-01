package dev.laubfrosch.archery.backend.scoring.score;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ScoreRepository  implements PanacheRepositoryBase<Score, String> {
}
