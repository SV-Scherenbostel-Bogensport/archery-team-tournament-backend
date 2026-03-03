package dev.laubfrosch.archery.backend.participant;

import dev.laubfrosch.archery.backend.tournament.TournamentRegistrationResponse;

import java.util.List;
import java.util.UUID;

public record TeamWithDetailsResponse(
    UUID id,
    String name,
    String contactEmail,
    Short expectedMembers,
    TournamentRegistrationResponse registration,
    List<TeamMemberResponse> members
) {}
