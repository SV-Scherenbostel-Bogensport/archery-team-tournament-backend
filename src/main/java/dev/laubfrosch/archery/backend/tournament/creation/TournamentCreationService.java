package dev.laubfrosch.archery.backend.tournament.creation;

import dev.laubfrosch.archery.backend.participant.Team;
import dev.laubfrosch.archery.backend.participant.TeamMember;
import dev.laubfrosch.archery.backend.tournament.Tournament;
import dev.laubfrosch.archery.backend.tournament.TournamentRegistration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class TournamentCreationService {

    @Transactional(rollbackOn = Exception.class)
    public TournamentCreateResponse create(TournamentCreateRequest req) {

        Tournament tournament = mapTournament(req);
        tournament.persist();

        if (req.getTeams() != null) {
            for (TournamentCreateRequest.TeamCreateRequest teamReq : req.getTeams()) {
                validateUniqueTeamName(tournament, teamReq.name());

                Team team = mapTeam(tournament, teamReq);
                team.persist();

                persistRegistration(team, teamReq.registration());
                persistMembers(team, teamReq.members());
            }
        }

        TournamentCreateResponse response = new TournamentCreateResponse();
        response.setTournamentId(tournament.getId());
        response.setName(tournament.getName());
        return response;
    }


    private Tournament mapTournament(TournamentCreateRequest req) {
        Tournament t = new Tournament();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setLocation(req.getLocation());
        t.setAddress(req.getAddress());
        t.setDate(req.getDate());
        t.setStartTime(req.getStartTime());
        t.setMaxSlots(req.getMaxSlots());
        t.setRegistrationDeadline(req.getRegistrationDeadline());
        t.setAllowRegistration(req.isAllowRegistration());
        t.setPrimaryColor(req.getPrimaryColor());
        t.setSecondaryColor(req.getSecondaryColor());
        t.setGenerated(false);
        return t;
    }

    private Team mapTeam(Tournament tournament, TournamentCreateRequest.TeamCreateRequest req) {
        Team team = new Team();
        team.setTournament(tournament);
        team.setName(req.name());
        team.setContactEmail(req.contactEmail());
        team.setTeamMemberCount(req.expectedMembers());
        return team;
    }

    private void persistRegistration(Team team, TournamentCreateRequest.RegistrationCreateRequest req) {
        TournamentRegistration reg = new TournamentRegistration();
        reg.setTeam(team);
        reg.setRegistration(req != null && req.registration() != null ? req.registration() : Instant.now());
        reg.setPayment(req != null ? req.payment() : null);
        reg.setArrival(req != null ? req.arrival() : null);
        reg.setNote(req != null ? req.note() : null);
        reg.persist();
    }

    private void persistMembers(Team team, List<TournamentCreateRequest.MemberCreateRequest> members) {
        if (members == null) return;
        for (TournamentCreateRequest.MemberCreateRequest memberReq : members) {
            TeamMember member = new TeamMember();
            member.setTeam(team);
            member.setFirstName(memberReq.firstName());
            member.setLastName(memberReq.lastName());
            member.setNumber(memberReq.number());
            member.persist();
        }
    }

    /**
     * Wirft HTTP 409, wenn ein Team mit diesem Namen im Turnier bereits existiert.
     */
    private void validateUniqueTeamName(Tournament tournament, String name) {
        long count = Team.count("tournament = ?1 and name = ?2", tournament, name);
        if (count > 0) {
            throw new TeamNameConflictException(name);
        }
    }
}