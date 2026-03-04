package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.api.dto.TournamentCreateRequest;
import dev.laubfrosch.archery.backend.api.dto.TournamentCreateResponse;
import dev.laubfrosch.archery.backend.api.dto.TournamentOverviewResponse;
import dev.laubfrosch.archery.backend.api.mapper.TournamentMapper;
import dev.laubfrosch.archery.backend.participant.*;
import dev.laubfrosch.archery.backend.tournament.exception.TeamNameConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class TournamentService {

    @Inject TournamentRepository tournamentRepository;
    @Inject TeamRepository teamRepository;
    @Inject TeamMemberRepository memberRepository;
    @Inject TournamentRegistrationRepository registrationRepository;
    @Inject TournamentMapper tournamentMapper;

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TournamentOverviewResponse> getOverview() {
        return tournamentRepository.listAll().stream().map(t -> {
            TournamentOverviewResponse dto = tournamentMapper.toOverview(t);
            // TeamCount berechnen wir manuell übers Repository
            dto.setTeamCount((int) teamRepository.count("tournament = ?1", t));
            return dto;
        }).toList();
    }

    @Transactional(rollbackOn = Exception.class)
    public TournamentCreateResponse create(TournamentCreateRequest req) {
        Tournament tournament = tournamentMapper.toEntity(req);
        tournamentRepository.persist(tournament);

        if (req.getTeams() != null) {
            for (TournamentCreateRequest.TeamCreateRequest teamReq : req.getTeams()) {
                validateUniqueTeamName(tournament, teamReq.name());

                Team team = new Team();
                team.setTournament(tournament);
                team.setName(teamReq.name());
                team.setContactEmail(teamReq.contactEmail());
                team.setExpectedMembers(teamReq.expectedMembers());
                teamRepository.persist(team);

                persistRegistration(team, teamReq.registration());
                persistMembers(team, teamReq.members());
            }
        }

        TournamentCreateResponse response = new TournamentCreateResponse();
        response.setTournamentId(tournament.getId());
        response.setName(tournament.getName());
        return response;
    }

    private void persistRegistration(Team team, TournamentCreateRequest.RegistrationCreateRequest req) {
        TournamentRegistration reg = new TournamentRegistration();
        reg.setTeam(team);
        reg.setRegistration(req != null && req.registration() != null ? req.registration() : Instant.now());
        reg.setPayment(req != null ? req.payment() : null);
        reg.setArrival(req != null ? req.arrival() : null);
        reg.setNote(req != null ? req.note() : null);
        registrationRepository.persist(reg);
    }

    private void persistMembers(Team team, List<TournamentCreateRequest.MemberCreateRequest> members) {
        if (members == null) return;
        for (TournamentCreateRequest.MemberCreateRequest memberReq : members) {
            TeamMember member = new TeamMember();
            member.setTeam(team);
            member.setFirstName(memberReq.firstName());
            member.setLastName(memberReq.lastName());
            member.setNumber(memberReq.number());
            memberRepository.persist(member);
        }
    }

    private void validateUniqueTeamName(Tournament tournament, String name) {
        long count = teamRepository.count("tournament = ?1 and name = ?2", tournament, name);
        if (count > 0) {
            throw new TeamNameConflictException(name);
        }
    }
}