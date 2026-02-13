package dev.laubfrosch.archery.backend.competition;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

//import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "stage_templates")
public class StageTemplate extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
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
}
