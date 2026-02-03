package dev.laubfrosch.att.backend;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Match getSourceMatch() {
        return sourceMatch;
    }

    public void setSourceMatch(Match sourceMatch) {
        this.sourceMatch = sourceMatch;
    }

    public Boolean getSourceMatchWinner() {
        return sourceMatchWinner;
    }

    public void setSourceMatchWinner(Boolean sourceMatchWinner) {
        this.sourceMatchWinner = sourceMatchWinner;
    }

    public Match getTargetMatch() {
        return targetMatch;
    }

    public void setTargetMatch(Match targetMatch) {
        this.targetMatch = targetMatch;
    }

    public Short getTargetMatchSlot() {
        return targetMatchSlot;
    }

    public void setTargetMatchSlot(Short targetMatchSlot) {
        this.targetMatchSlot = targetMatchSlot;
    }

}