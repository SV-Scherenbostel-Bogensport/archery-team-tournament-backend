package dev.laubfrosch.archery.backend.competition.stage;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Cacheable
@Table(name = "stage_modes")
public class StageMode extends PanacheEntityBase {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private StageModeId id;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
