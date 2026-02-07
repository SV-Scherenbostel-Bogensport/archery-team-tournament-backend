package dev.laubfrosch.archery.backend.scoring;

import dev.laubfrosch.archery.backend.competition.Match;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "sets")
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @NotNull
    @Column(name = "set_index", nullable = false)
    private Short setIndex;

    @Column(name = "total_team1")
    private Long totalTeam1;

    @Column(name = "total_team2")
    private Long totalTeam2;

    @Column(name = "points_team1")
    private Long pointsTeam1;

    @Column(name = "points_team2")
    private Long pointsTeam2;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}