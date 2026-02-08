package dev.laubfrosch.archery.backend.competition;

import dev.laubfrosch.archery.backend.tournament.Status;
import dev.laubfrosch.archery.backend.participant.Team;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "matches")
public class Match extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "shoot_off", nullable = false)
    private Boolean shootOff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id")
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id")
    private Team team2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target1_id")
    private Target target1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target2_id")
    private Target target2;

    @Column(name = "updated_at")
    private Instant updatedAt;
}