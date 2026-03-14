package dev.laubfrosch.archery.backend.participant;

import dev.laubfrosch.archery.backend.api.dto.TeamUpdateRequest;
import dev.laubfrosch.archery.backend.api.dto.TeamWithDetailsDto;
import dev.laubfrosch.archery.backend.api.mapper.TeamMapper;
import dev.laubfrosch.archery.backend.competition.match.Match;
import dev.laubfrosch.archery.backend.competition.match.MatchTransition;
import dev.laubfrosch.archery.backend.competition.round.Round;
import dev.laubfrosch.archery.backend.competition.stage.*;
import dev.laubfrosch.archery.backend.scoring.target.Target;
import dev.laubfrosch.archery.backend.shared.status.Status;
import dev.laubfrosch.archery.backend.shared.status.StatusId;
import dev.laubfrosch.archery.backend.tournament.Tournament;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistration;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistrationRepository;
import dev.laubfrosch.archery.backend.tournament.TournamentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ApplicationScoped
public class TeamService {

    @Inject TeamRepository teamRepository;
    @Inject TournamentRepository tournamentRepository;
    @Inject TeamMemberRepository memberRepository;
    @Inject TournamentRegistrationRepository registrationRepository;
    @Inject TeamMapper teamMapper;

    public List<Team> getTeamsByTournament(UUID tournamentId) {
        return teamRepository.list("tournament.id", tournamentId);
    }

    public List<TeamWithDetailsDto> getTeamsWithDetails(UUID tournamentId) {
        List<Team> teams = getTeamsByTournament(tournamentId);
        if (teams.isEmpty()) return List.of();

        List<TournamentRegistration> registrations = registrationRepository.list("team.tournament.id", tournamentId);
        List<TeamMember> allMembers = memberRepository.list("team.tournament.id", tournamentId);

        Map<UUID, TournamentRegistration> regMap = registrations.stream()
                .collect(Collectors.toMap(r -> r.getTeam().getId(), r -> r));

        Map<UUID, List<TeamMember>> membersByTeamId = allMembers.stream()
                .collect(Collectors.groupingBy(m -> m.getTeam().getId()));

        return teams.stream()
                .map(team -> teamMapper.toTeamWithDetails(
                        team,
                        regMap.get(team.getId()),
                        membersByTeamId.getOrDefault(team.getId(), List.of())
                ))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackOn = Exception.class)
    public List<TeamWithDetailsDto> updateTeamsForTournament(UUID tournamentId, List<TeamUpdateRequest> requests) {
        Tournament tournament = tournamentRepository.findByIdOptional(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        List<Team> existingTeams = teamRepository.list("tournament.id", tournamentId);
        Set<UUID> existingTeamIds = existingTeams.stream()
                .map(Team::getId)
                .collect(Collectors.toSet());

        // Nur valide UUIDs die auch in DB existieren
        Set<UUID> requestedTeamIds = requests.stream()
                .map(TeamUpdateRequest::getId)
                .filter(id -> id != null && isValidUuid(id))
                .map(UUID::fromString)
                .filter(existingTeamIds::contains)
                .collect(Collectors.toSet());

        // Teams die nicht mehr im Request sind → löschen
        for (Team team : existingTeams) {
            if (!requestedTeamIds.contains(team.getId())) {
                memberRepository.delete("team.id", team.getId());
                registrationRepository.delete("team.id", team.getId());
                teamRepository.delete(team);
            }
        }

        List<TeamWithDetailsDto> result = new ArrayList<>();

        for (TeamUpdateRequest req : requests) {
            boolean isNew = req.getId() == null
                    || !isValidUuid(req.getId())
                    || !existingTeamIds.contains(UUID.fromString(req.getId()));

            Team team;
            if (isNew) {
                team = new Team();
                team.setTournament(tournament);
                team.setName(req.getName());
                team.setContactEmail(req.getContactEmail());
                team.setExpectedMembers(req.getExpectedMembers());
                teamRepository.persist(team);
            } else {
                team = teamRepository.findByIdOptional(UUID.fromString(req.getId())).orElseThrow();
                if (req.getName() != null && !req.getName().equals(team.getName())) {
                    team.setName(req.getName());
                }
                if (req.getContactEmail() != null) team.setContactEmail(req.getContactEmail());
                if (req.getExpectedMembers() != null) team.setExpectedMembers(req.getExpectedMembers());
            }

            // Registrierung
            TournamentRegistration reg = isNew ? null :
                    registrationRepository.find("team.id", team.getId()).singleResultOptional().orElse(null);

            if (reg == null) {
                reg = new TournamentRegistration();
                reg.setTeam(team);
                reg.setRegistration(Instant.now());
            }
            if (req.getTournamentRegistration() != null) {
                TeamUpdateRequest.RegistrationUpdateRequest regReq = req.getTournamentRegistration();
                if (regReq.registration() != null) reg.setRegistration(regReq.registration());
                reg.setPayment(regReq.payment());
                reg.setArrival(regReq.arrival());
                reg.setNote(regReq.note());
            }
            registrationRepository.persist(reg);

            // Members: Diff
            List<TeamMember> existingMembers = isNew
                    ? List.of()
                    : memberRepository.list("team.id", team.getId());

            Set<UUID> existingMemberIds = existingMembers.stream()
                    .map(TeamMember::getId)
                    .collect(Collectors.toSet());

            Set<UUID> requestedMemberIds = req.getMembers() == null ? Set.of() :
                    req.getMembers().stream()
                            .map(TeamUpdateRequest.MemberUpdateRequest::id)
                            .filter(id -> id != null && isValidUuid(id))
                            .map(UUID::fromString)
                            .filter(existingMemberIds::contains)
                            .collect(Collectors.toSet());

            // Members löschen die nicht mehr im Request sind
            for (TeamMember member : existingMembers) {
                if (!requestedMemberIds.contains(member.getId())) {
                    memberRepository.delete(member);
                }
            }
            memberRepository.flush();

            // Members updaten oder neu anlegen
            if (req.getMembers() != null) {
                for (TeamUpdateRequest.MemberUpdateRequest memberReq : req.getMembers()) {
                    boolean isNewMember = memberReq.id() == null
                            || !isValidUuid(memberReq.id())
                            || !existingMemberIds.contains(UUID.fromString(memberReq.id()));
                    if (isNewMember) {
                        TeamMember member = new TeamMember();
                        member.setTeam(team);
                        member.setFirstName(memberReq.firstName());
                        member.setLastName(memberReq.lastName());
                        member.setNumber(memberReq.number());
                        memberRepository.persist(member);
                    } else {
                        TeamMember member = existingMembers.stream()
                                .filter(m -> m.getId().equals(UUID.fromString(memberReq.id())))
                                .findFirst().orElseThrow();
                        member.setFirstName(memberReq.firstName());
                        member.setLastName(memberReq.lastName());
                        member.setNumber(memberReq.number());
                    }
                }
            }

            List<TeamMember> updatedMembers = memberRepository.list("team.id", team.getId());
            result.add(teamMapper.toTeamWithDetails(team, reg, updatedMembers));
        }

        return result;
    }

    private boolean isValidUuid(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}