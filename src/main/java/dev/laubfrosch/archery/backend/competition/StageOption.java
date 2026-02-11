package dev.laubfrosch.archery.backend.competition;

import dev.laubfrosch.archery.backend.scoring.TargetFace;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "stage_options")
public class StageOption extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_face_id", nullable = false)
    private TargetFace targetFace;

    @Column(name = "distance")
    private Integer distance;

    @Column(name = "shooting_time")
    private Integer shootingTime;

    @Column(name = "arrows_per_member")
    private Integer arrowsPerMember;

    @Column(name = "members_per_match")
    private Integer membersPerMatch;
}