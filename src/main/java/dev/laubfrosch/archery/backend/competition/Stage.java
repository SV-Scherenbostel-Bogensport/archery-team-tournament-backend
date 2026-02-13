package dev.laubfrosch.archery.backend.competition;

import dev.laubfrosch.archery.backend.tournament.Status;
import dev.laubfrosch.archery.backend.tournament.Tournament;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "stages")
public class Stage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_mode_id")
    private StageMode stageMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ranking_method_id")
    private RankingMethod rankingMethod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stage_config", columnDefinition = "jsonb")
    private StageConfig stageConfig;
    //private Map<String, Object> stageConfig;

    @Column(name = "stage_index")
    private Short roundIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_stage_id")
    private Stage parentStage;
}
