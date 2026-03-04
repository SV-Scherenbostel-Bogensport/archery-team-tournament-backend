package dev.laubfrosch.archery.backend.api.dto;

import java.time.Instant;

public record TournamentRegistrationDto(
    Instant registration,
    Instant payment,
    Instant arrival,
    String note
) {}
