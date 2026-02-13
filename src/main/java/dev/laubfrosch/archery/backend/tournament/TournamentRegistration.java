package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.participant.Team;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tournament_registrations")
public class TournamentRegistration extends PanacheEntityBase {

    @Id
    @Column(name = "team_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team teams;

    @CreationTimestamp
    @ColumnDefault("now()")
    @Column(name = "registration_date")
    private Instant registrationDate;

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
