package dev.laubfrosch.archery.backend.scoring.targetface;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "target_faces")
public class TargetFace extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Size(max = 16)
    @Column(name = "size")
    private String size;

    @Column(name = "image")
    private byte[] image;
}