package dev.laubfrosch.att.backend;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Entity
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Short getSetIndex() {
        return setIndex;
    }

    public void setSetIndex(Short setIndex) {
        this.setIndex = setIndex;
    }

    public Long getTotalTeam1() {
        return totalTeam1;
    }

    public void setTotalTeam1(Long totalTeam1) {
        this.totalTeam1 = totalTeam1;
    }

    public Long getTotalTeam2() {
        return totalTeam2;
    }

    public void setTotalTeam2(Long totalTeam2) {
        this.totalTeam2 = totalTeam2;
    }

    public Long getPointsTeam1() {
        return pointsTeam1;
    }

    public void setPointsTeam1(Long pointsTeam1) {
        this.pointsTeam1 = pointsTeam1;
    }

    public Long getPointsTeam2() {
        return pointsTeam2;
    }

    public void setPointsTeam2(Long pointsTeam2) {
        this.pointsTeam2 = pointsTeam2;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}