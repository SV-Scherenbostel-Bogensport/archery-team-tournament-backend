package dev.laubfrosch.archery.backend.scoring;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "scores")
public class Score extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 16)
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull
    @Column(name = "value", nullable = false)
    private Integer value;

    @Size(min = 7, max = 7)
    @Column(name = "color", length = 7)
    @JdbcTypeCode(Types.CHAR)
    private String color;
}