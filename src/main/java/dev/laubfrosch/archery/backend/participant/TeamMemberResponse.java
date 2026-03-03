package dev.laubfrosch.archery.backend.participant;

import java.util.UUID;

public record TeamMemberResponse(
    UUID id,
    String firstName,
    String lastName,
    Short number
) {}
