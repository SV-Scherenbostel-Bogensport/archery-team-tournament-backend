package dev.laubfrosch.archery.backend.tournament;

import java.time.Instant;

public record TournamentRegistrationResponse(
    Instant registration,
    Instant payment,
    Instant arrival,
    String note
) {}
