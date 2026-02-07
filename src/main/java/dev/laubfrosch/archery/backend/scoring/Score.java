package dev.laubfrosch.archery.backend.scoring;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "scores")
public class Score {

    @Id
    @Size(max = 128)
    @Column(name = "score_code", nullable = false, length = 128)
    private String scoreCode;

    @NotNull
    @Column(name = "value", nullable = false)
    private Integer value;

    @Size(max = 7)
    @Column(name = "color", length = 7)
    private String color;
}