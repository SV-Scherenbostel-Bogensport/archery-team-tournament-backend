package dev.laubfrosch.archery.backend.competition;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Cacheable
@Table(name = "ranking_methods")
public class RankingMethod {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RankingMethodId id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
