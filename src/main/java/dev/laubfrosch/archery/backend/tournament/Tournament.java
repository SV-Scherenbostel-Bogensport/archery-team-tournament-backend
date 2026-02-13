package dev.laubfrosch.archery.backend.tournament;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tournaments")
public class Tournament extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 255)
    @Column(name = "location")
    private String location;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "max_slots")
    private Short maxSlots;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    @NotNull
    @Column(name = "allow_registration")
    private Boolean allowRegistration = false;

    @Size(min = 7, max = 7)
    @Column(name = "primary_color", length = 7)
    @JdbcTypeCode(Types.CHAR)
    private String primaryColor;

    @Size(min = 7, max = 7)
    @Column(name = "secondary_color", length = 7)
    @JdbcTypeCode(Types.CHAR)
    private String secondaryColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_ranking_strategy_id", length = 50)
    private FinalRankingStrategyId finalRankingStrategy;

    @NotNull
    @Column(name = "generated")
    private Boolean generated = false;
}
