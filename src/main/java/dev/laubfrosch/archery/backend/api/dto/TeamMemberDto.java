package dev.laubfrosch.archery.backend.api.dto;

import java.util.UUID;

public record TeamMemberDto(
    UUID id,
    String firstName,
    String lastName,
    Short number
) {}
