package dev.laubfrosch.archery.backend.participant;

import dev.laubfrosch.archery.backend.tournament.TournamentRegistration;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistrationResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class TeamService {

    @Inject
    TeamRepository teamRepository;

    public List<Team> getTeamsByTournament(UUID tournamentId) {
        return teamRepository.list("tournament.id", tournamentId);
    }

    public List<TeamWithDetailsResponse> getTeamsWithDetails(UUID tournamentId) {
        // 1. Alle Teams des Turniers laden (Query 1)
        List<Team> teams = Team.list("tournament.id", tournamentId);

        if (teams.isEmpty()) {
            return List.of();
        }

        // 2. Alle Registrierungen für dieses Turnier laden (Query 2)
        // Über "team.tournament.id" greifen wir direkt auf die Relationen zu
        List<TournamentRegistration> registrations = TournamentRegistration.list("team.tournament.id", tournamentId);

        // In eine Map umwandeln für schnellen Zugriff: Team-ID -> Registration
        Map<UUID, TournamentRegistration> regMap = registrations.stream()
                .collect(Collectors.toMap(r -> r.getTeam().getId(), r -> r));

        // 3. Alle Mitglieder für dieses Turnier laden (Query 3)
        List<TeamMember> allMembers = TeamMember.list("team.tournament.id", tournamentId);

        // In eine Map gruppieren: Team-ID -> Liste von TeamMembers
        Map<UUID, List<TeamMember>> membersByTeamId = allMembers.stream()
                .collect(Collectors.groupingBy(m -> m.getTeam().getId()));

        // 4. Alles im Speicher zusammensetzen und Mappen
        return teams.stream().map(team -> {
            UUID teamId = team.getId();

            // Registration mappen (falls vorhanden)
            TournamentRegistration reg = regMap.get(teamId);
            TournamentRegistrationResponse regResponse = null;
            if (reg != null) {
                regResponse = new TournamentRegistrationResponse(
                        reg.getRegistration(),
                        reg.getPayment(),
                        reg.getArrival(),
                        reg.getNote()
                );
            }

            // Members mappen (falls keine da sind, leere Liste nehmen)
            List<TeamMember> teamMembers = membersByTeamId.getOrDefault(teamId, List.of());
            List<TeamMemberResponse> memberResponses = teamMembers.stream()
                    .map(m -> new TeamMemberResponse(
                            m.getId(),
                            m.getFirstName(),
                            m.getLastName(),
                            m.getNumber()
                    ))
                    .collect(Collectors.toList());

            // Fertiges DTO bauen
            return new TeamWithDetailsResponse(
                    teamId,
                    team.getName(),
                    team.getContactEmail(),
                    team.getTeamMemberCount(),
                    regResponse,
                    memberResponses
            );
        }).collect(Collectors.toList());
    }
}
