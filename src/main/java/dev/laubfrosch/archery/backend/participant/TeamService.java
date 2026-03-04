package dev.laubfrosch.archery.backend.participant;

import dev.laubfrosch.archery.backend.api.dto.TeamWithDetailsDto;
import dev.laubfrosch.archery.backend.api.mapper.TeamMapper;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistration;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistrationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TeamService {

    @Inject TeamRepository teamRepository;
    @Inject TeamMemberRepository memberRepository;
    @Inject TournamentRegistrationRepository registrationRepository;
    @Inject TeamMapper teamMapper;

    public List<Team> getTeamsByTournament(UUID tournamentId) {
        return teamRepository.list("tournament.id", tournamentId);
    }

    public List<TeamWithDetailsDto> getTeamsWithDetails(UUID tournamentId) {
        List<Team> teams = getTeamsByTournament(tournamentId);
        if (teams.isEmpty()) return List.of();

        // Alle Daten für das Turnier in einem Rutsch (Performance)
        List<TournamentRegistration> registrations = registrationRepository.list("team.tournament.id", tournamentId);
        List<TeamMember> allMembers = memberRepository.list("team.tournament.id", tournamentId);

        // Mapping-Helfer (Maps)
        Map<UUID, TournamentRegistration> regMap = registrations.stream()
                .collect(Collectors.toMap(r -> r.getTeam().getId(), r -> r));

        Map<UUID, List<TeamMember>> membersByTeamId = allMembers.stream()
                .collect(Collectors.groupingBy(m -> m.getTeam().getId()));

        // Mapper nutzen
        return teams.stream()
                .map(team -> teamMapper.toTeamWithDetails(
                        team,
                        regMap.get(team.getId()),
                        membersByTeamId.getOrDefault(team.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }
}