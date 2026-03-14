package dev.laubfrosch.archery.backend.api.dto;

import dev.laubfrosch.archery.backend.shared.status.Status;

import java.util.List;
import java.util.UUID;

public record StageStatusDto (
        UUID id,
        String name,
        Status status,
        List<RoundStatusDto> rounds
) {}
