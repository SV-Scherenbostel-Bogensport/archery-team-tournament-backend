package dev.laubfrosch.att.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
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

    public String getScoreCode() {
        return scoreCode;
    }

    public void setScoreCode(String scoreCode) {
        this.scoreCode = scoreCode;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}