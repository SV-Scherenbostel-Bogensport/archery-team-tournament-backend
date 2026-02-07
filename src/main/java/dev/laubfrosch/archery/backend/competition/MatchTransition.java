package dev.laubfrosch.archery.backend.competition;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "match_transitions")
public class MatchTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_match_id", nullable = false)
    private Match sourceMatch;

    @NotNull
    @Column(name = "source_match_winner", nullable = false)
    private Boolean sourceMatchWinner;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_match_id", nullable = false)
    private Match targetMatch;

    @NotNull
    @Column(name = "target_match_slot", nullable = false)
    private Short targetMatchSlot;
}