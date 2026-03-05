package dev.laubfrosch.archery.backend.api.dto;

import java.util.List;
import java.util.UUID;

public record TeamWithDetailsDto(
    UUID id,
    String name,
    String contactEmail,
    Short expectedMembers,
    TournamentRegistrationDto tournamentRegistration,
    List<TeamMemberDto> members
) {}
