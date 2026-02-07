package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.participant.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "tournament_registrations")
public class TournamentRegistration {

    @Id
    @Column(name = "team_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team teams;

    @ColumnDefault("now()")
    @Column(name = "registration_date")
    private Instant registrationDate;

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "note", length = Integer.MAX_VALUE)
    private String note;
}