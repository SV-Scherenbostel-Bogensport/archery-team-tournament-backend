package dev.laubfrosch.archery.backend.tournament;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Cacheable
@Table(name = "final_ranking_strategies")
public class FinalRankingStrategy {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private FinalRankingStrategyId id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
