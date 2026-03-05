package dev.laubfrosch.archery.backend.api.mapper;

import dev.laubfrosch.archery.backend.api.dto.TeamMemberDto;
import dev.laubfrosch.archery.backend.api.dto.TournamentRegistrationDto;
import dev.laubfrosch.archery.backend.api.dto.TeamWithDetailsDto;
import dev.laubfrosch.archery.backend.participant.Team;
import dev.laubfrosch.archery.backend.participant.TeamMember;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "jakarta")
public interface TeamMapper {

    @Mapping(target = "id", source = "team.id")
    @Mapping(target = "tournamentRegistration", source = "reg")
    @Mapping(target = "members", source = "members")
    TeamWithDetailsDto toTeamWithDetails(Team team, TournamentRegistration reg, List<TeamMember> members);

    TournamentRegistrationDto toRegistrationDto(TournamentRegistration reg);

    TeamMemberDto toMemberDto(TeamMember member);
}