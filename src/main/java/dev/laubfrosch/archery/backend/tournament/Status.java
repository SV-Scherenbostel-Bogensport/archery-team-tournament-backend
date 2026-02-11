package dev.laubfrosch.archery.backend.tournament;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Getter
@Setter
@Cacheable
@Table(name = "status")
public class Status extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Size(min = 7, max = 7)
    @Column(name = "primary_color", length = 7)
    @JdbcTypeCode(Types.CHAR)
    private String primaryColor;

    @Size(min = 7, max = 7)
    @Column(name = "secondary_color", length = 7)
    @JdbcTypeCode(Types.CHAR)
    private String secondaryColor;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "pulsing", nullable = false)
    private Boolean pulsing;

}