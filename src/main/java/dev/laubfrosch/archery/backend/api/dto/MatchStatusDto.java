package dev.laubfrosch.archery.backend.api.dto;

import dev.laubfrosch.archery.backend.shared.status.Status;

import java.util.UUID;

public record MatchStatusDto (
        UUID id,
        String name,
        Status status
) {}
