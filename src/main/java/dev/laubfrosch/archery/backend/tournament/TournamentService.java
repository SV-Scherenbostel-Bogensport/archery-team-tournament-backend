package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.api.dto.*;
import dev.laubfrosch.archery.backend.api.mapper.TournamentMapper;
import dev.laubfrosch.archery.backend.competition.match.Match;
import dev.laubfrosch.archery.backend.competition.match.MatchRepository;
import dev.laubfrosch.archery.backend.competition.match.MatchTransition;
import dev.laubfrosch.archery.backend.competition.round.Round;
import dev.laubfrosch.archery.backend.competition.round.RoundRepository;
import dev.laubfrosch.archery.backend.competition.stage.*;
import dev.laubfrosch.archery.backend.participant.*;
import dev.laubfrosch.archery.backend.scoring.target.Target;
import dev.laubfrosch.archery.backend.shared.status.Status;
import dev.laubfrosch.archery.backend.shared.status.StatusId;
import dev.laubfrosch.archery.backend.tournament.exception.TeamNameConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class TournamentService {

    @Inject
    TournamentRepository tournamentRepository;

    @Inject
    TeamRepository teamRepository;

    @Inject
    TeamMemberRepository memberRepository;

    @Inject
    TournamentRegistrationRepository registrationRepository;

    @Inject
    StageRepository stageRepository;

    @Inject
    RoundRepository roundRepository;

    @Inject
    MatchRepository matchRepository;

    @Inject
    TournamentMapper tournamentMapper;

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TournamentOverviewResponse> getOverview() {
        return tournamentRepository.listAll().stream().map(t -> {
            TournamentOverviewResponse dto = tournamentMapper.toOverview(t);
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

    @Transactional
    public void generateTournament(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findByIdOptional(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        if (tournament.getGenerated()) {
            throw new BadRequestException("Tournament is already generated");
        }

        List<Team> rawTeams = teamRepository.list("tournament.id", tournamentId);
        Map<Integer, Team> teams = new HashMap<>();
        for (int i = 1; i <= rawTeams.size(); i++) {
            teams.put(i, rawTeams.get(i - 1));
        }

        Status plannedStatus = Status.findById(StatusId.PLANNED);
        Status unknownStatus = Status.findById(StatusId.UNKNOWN);

        // Targets
        Map<Integer, Target> targets = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            Target target = new Target(null, tournament, String.valueOf(i));
            targets.put(i, target);
        }
        Target.persist(targets.values());

        // Stages
        StageMode roundRobinStageMode = StageMode.findById(StageModeId.ROUND_ROBIN);
        StageMode doubleEliminationStageMode = StageMode.findById(StageModeId.DOUBLE_ELIMINATION);

        RankingMethod pointsRankingMethod = RankingMethod.findById(RankingMethodId.POINTS);
        RankingMethod bracketPosRankingMethod = RankingMethod.findById(RankingMethodId.BRACKET_POS);

        Stage qualificationStage = new Stage(null, tournament, plannedStatus, "Qualifikation", roundRobinStageMode, pointsRankingMethod, null, (short) 1, null);
        Stage knockoutStage = new Stage(null, tournament, plannedStatus, "Doppelt-KO-Finale", doubleEliminationStageMode, bracketPosRankingMethod, null, (short) 2, qualificationStage);
        Stage.persist(qualificationStage, knockoutStage);

        Map<Integer, Round> qualificationRounds = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            Round round = new Round(null, qualificationStage, plannedStatus, i + ". Qualifikationsrunde", (short) i, null);
            qualificationRounds.put(i, round);
        }
        Round.persist(qualificationRounds.values());

        Map<Integer, Round> knockoutRounds = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            Round round = new Round(null, knockoutStage, plannedStatus, i + ". Finalrunde", (short) i, null);
            knockoutRounds.put(i, round);
        }
        Round.persist(knockoutRounds.values());

        // Matches
        List<Match> qualiMatches = new ArrayList<>();

        // === 1. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(1), plannedStatus, "1. Qualifikationsmatch", false, teams.get(5), teams.get(4), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(1), plannedStatus, "1. Qualifikationsmatch", false, teams.get(2), teams.get(7), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(1), plannedStatus, "1. Qualifikationsmatch", false, teams.get(1), teams.get(8), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(1), plannedStatus, "1. Qualifikationsmatch", false, teams.get(3), teams.get(6), null, targets.get(7), targets.get(8), null));

        // === 2. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(2), plannedStatus, "2. Qualifikationsmatch", false, teams.get(3), teams.get(5), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(2), plannedStatus, "2. Qualifikationsmatch", false, teams.get(8), teams.get(4), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(2), plannedStatus, "2. Qualifikationsmatch", false, teams.get(7), teams.get(1), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(2), plannedStatus, "2. Qualifikationsmatch", false, teams.get(6), teams.get(2), null, targets.get(7), targets.get(8), null));

        // === 3. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(3), plannedStatus, "3. Qualifikationsmatch", false, teams.get(4), teams.get(7), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(3), plannedStatus, "3. Qualifikationsmatch", false, teams.get(1), teams.get(6), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(3), plannedStatus, "3. Qualifikationsmatch", false, teams.get(2), teams.get(5), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(3), plannedStatus, "3. Qualifikationsmatch", false, teams.get(8), teams.get(3), null, targets.get(7), targets.get(8), null));

        // === 4. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(4), plannedStatus, "4. Qualifikationsmatch", false, teams.get(8), teams.get(2), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(4), plannedStatus, "4. Qualifikationsmatch", false, teams.get(7), teams.get(3), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(4), plannedStatus, "4. Qualifikationsmatch", false, teams.get(6), teams.get(4), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(4), plannedStatus, "4. Qualifikationsmatch", false, teams.get(1), teams.get(5), null, targets.get(7), targets.get(8), null));

        // === 5. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(5), plannedStatus, "5. Qualifikationsmatch", false, teams.get(7), teams.get(6), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(5), plannedStatus, "5. Qualifikationsmatch", false, teams.get(5), teams.get(8), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(5), plannedStatus, "5. Qualifikationsmatch", false, teams.get(3), teams.get(2), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(5), plannedStatus, "5. Qualifikationsmatch", false, teams.get(4), teams.get(1), null, targets.get(7), targets.get(8), null));

        // === 6. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(6), plannedStatus, "6. Qualifikationsmatch", false, teams.get(1), teams.get(3), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(6), plannedStatus, "6. Qualifikationsmatch", false, teams.get(4), teams.get(2), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(6), plannedStatus, "6. Qualifikationsmatch", false, teams.get(8), teams.get(6), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(6), plannedStatus, "6. Qualifikationsmatch", false, teams.get(5), teams.get(7), null, targets.get(7), targets.get(8), null));

        // === 7. Qualifikationsrunde ===
        qualiMatches.add(new Match(null, qualificationRounds.get(7), plannedStatus, "7. Qualifikationsmatch", false, teams.get(2), teams.get(1), null, targets.get(1), targets.get(2), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(7), plannedStatus, "7. Qualifikationsmatch", false, teams.get(6), teams.get(5), null, targets.get(3), targets.get(4), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(7), plannedStatus, "7. Qualifikationsmatch", false, teams.get(4), teams.get(3), null, targets.get(5), targets.get(6), null));
        qualiMatches.add(new Match(null, qualificationRounds.get(7), plannedStatus, "7. Qualifikationsmatch", false, teams.get(7), teams.get(8), null, targets.get(7), targets.get(8), null));

        Match.persist(qualiMatches);

        // === 1. Finalrunde ===
        Match ko1 = new Match(null, knockoutRounds.get(1), plannedStatus, "[Ko1] Winners Bracket: Eingangsrunde: Q1 vs Q8", true, null, null, null, targets.get(1), targets.get(2), null);
        Match ko2 = new Match(null, knockoutRounds.get(1), plannedStatus, "[Ko2] Winners Bracket: Eingangsrunde: Q5 vs Q4", true, null, null, null, targets.get(3), targets.get(4), null);
        Match ko3 = new Match(null, knockoutRounds.get(1), plannedStatus, "[Ko3] Winners Bracket: Eingangsrunde: Q3 vs Q6", true, null, null, null, targets.get(5), targets.get(6), null);
        Match ko4 = new Match(null, knockoutRounds.get(1), plannedStatus, "[Ko4] Winners Bracket: Eingangsrunde: Q7 vs Q2", true, null, null, null, targets.get(7), targets.get(8), null);

        // === 2. Finalrunde ===
        Match ko5 = new Match(null, knockoutRounds.get(2), plannedStatus, "[Ko5] Losers Bracket: L-Ko1 vs L-Ko2", true, null, null, null, targets.get(1), targets.get(2), null);
        Match ko6 = new Match(null, knockoutRounds.get(2), plannedStatus, "[Ko6] Winners Bracket: W-Ko1 vs W-Ko2", true, null, null, null, targets.get(3), targets.get(4), null);
        Match ko7 = new Match(null, knockoutRounds.get(2), plannedStatus, "[Ko7] Winners Bracket: W-Ko3 vs W-Ko4", true, null, null, null, targets.get(5), targets.get(6), null);
        Match ko8 = new Match(null, knockoutRounds.get(2), plannedStatus, "[Ko8] Losers Bracket: L-Ko3 vs L-Ko4", true, null, null, null, targets.get(7), targets.get(8), null);

        // === 3. Finalrunde ===
        Match ko9 = new Match(null, knockoutRounds.get(3), plannedStatus, "[Ko9] Losers Bracket: L-Ko7 vs W-Ko5", true, null, null, null, targets.get(1), targets.get(2), null);
        Match ko10 = new Match(null, knockoutRounds.get(3), plannedStatus, "[Ko10] Losers Bracket: L-Ko6 vs W-Ko8", true, null, null, null, targets.get(5), targets.get(6), null);
        Match ko11 = new Match(null, knockoutRounds.get(3), plannedStatus, "[Ko11] Losers Bracket: Spiel um Platz 7 & 8: L-Ko8 vs L-Ko5", true, null, null, null, targets.get(3), targets.get(4), null);

        // === 4. Finalrunde ===
        Match ko12 = new Match(null, knockoutRounds.get(4), plannedStatus, "[Ko12] Winners Bracket: W-Ko6 vs W-Ko7", true, null, null, null, targets.get(1), targets.get(2), null);
        Match ko13 = new Match(null, knockoutRounds.get(4), plannedStatus, "[Ko13] Losers Bracket: Spiel um Platz 4: W-Ko9 vs W-Ko10", true, null, null, null, targets.get(5), targets.get(6), null);
        Match ko14 = new Match(null, knockoutRounds.get(4), plannedStatus, "[Ko14] Losers Bracket: Spiel um Platz 5 & 6: L-Ko9 vs L-Ko10",true, null, null, null, targets.get(3), targets.get(4), null);

        // === 5. Finalrunde ===
        Match ko15 = new Match(null, knockoutRounds.get(5), plannedStatus, "[Ko15] Losers Bracket: Spiel um Platz 3: L-Ko12 vs W-Ko13", true, null, null, null, targets.get(3), targets.get(4), null);

        // === 6. Finalrunde ===
        Match ko16 = new Match(null, knockoutRounds.get(6), plannedStatus, "[Ko16] Finale: W-Ko12 vs W-Ko15", true, null, null, null, targets.get(3), targets.get(4), null);

        // === 7. Finalrunde ===
        Match ko17 = new Match(null, knockoutRounds.get(7), unknownStatus, "[Ko17] 2. Finale: W-Ko16 vs L-Ko16", true, null, null, null, targets.get(3), targets.get(4), null);
        knockoutRounds.get(7).setStatus(unknownStatus);

        Match.persist(ko1, ko2, ko3, ko4, ko5, ko6, ko7, ko8, ko9, ko10, ko11, ko12, ko13, ko14, ko15, ko16, ko17);

        // === Transitions ===
        List<MatchTransition> transitions = new ArrayList<>();

        // Ko1 → Ko6 (Gewinner), Ko5 (Verlierer)
        //noinspection DuplicatedCode
        transitions.add(new MatchTransition(null, ko1, true, ko6, (short) 1));
        transitions.add(new MatchTransition(null, ko1, false, ko5, (short) 1));

        // Ko2 → Ko6 (Gewinner), Ko5 (Verlierer)
        //noinspection DuplicatedCode
        transitions.add(new MatchTransition(null, ko2, true,  ko6, (short) 2));
        transitions.add(new MatchTransition(null, ko2, false, ko5, (short) 2));

        // Ko3 → Ko7 (Gewinner), Ko8 (Verlierer)
        transitions.add(new MatchTransition(null, ko3, true,  ko7, (short) 1));
        transitions.add(new MatchTransition(null, ko3, false, ko8, (short) 1));

        // Ko4 → Ko7 (Gewinner), Ko8 (Verlierer)
        transitions.add(new MatchTransition(null, ko4, true,  ko7, (short) 2));
        transitions.add(new MatchTransition(null, ko4, false, ko8, (short) 2));

        // Ko5 → Ko9 (Gewinner), Ko11 (Verlierer → Platz 7/8)
        //noinspection DuplicatedCode
        transitions.add(new MatchTransition(null, ko5, true,  ko9,  (short) 2));
        transitions.add(new MatchTransition(null, ko5, false, ko11, (short) 2));

        // Ko6 → Ko12 (Gewinner), Ko10 (Verlierer)
        transitions.add(new MatchTransition(null, ko6, true,  ko12, (short) 1));
        transitions.add(new MatchTransition(null, ko6, false, ko10, (short) 1));

        // Ko7 → Ko12 (Gewinner), Ko9 (Verlierer)
        transitions.add(new MatchTransition(null, ko7, true,  ko12, (short) 2));
        transitions.add(new MatchTransition(null, ko7, false, ko9,  (short) 1));

        // Ko8 → Ko10 (Gewinner), Ko11 (Verlierer → Platz 7/8)
        transitions.add(new MatchTransition(null, ko8, true,  ko10, (short) 2));
        transitions.add(new MatchTransition(null, ko8, false, ko11, (short) 1));

        // Ko9 → Ko13 (Gewinner → Platz 4), Ko14 (Verlierer → Platz 5/6)
        //noinspection DuplicatedCode
        transitions.add(new MatchTransition(null, ko9, true, ko13, (short) 1));
        transitions.add(new MatchTransition(null, ko9, false, ko14, (short) 1));

        // Ko10 → Ko13 (Gewinner), Ko14 (Verlierer → Platz 5/6)
        transitions.add(new MatchTransition(null, ko10, true, ko13, (short) 2));
        transitions.add(new MatchTransition(null, ko10, false, ko14, (short) 2));

        // Ko12 → Ko16 (Gewinner), Ko15 (Verlierer)
        transitions.add(new MatchTransition(null, ko12, true,  ko16, (short) 1));
        transitions.add(new MatchTransition(null, ko12, false, ko15, (short) 1));

        // Ko13 → Ko15 (Gewinner), ausgeschieden (Verlierer) (Platz 4)
        transitions.add(new MatchTransition(null, ko13, true,  ko15, (short) 2));

        // Ko15 → Ko16 (Gewinner), ausgeschieden (Verlierer) (Platz 3)
        transitions.add(new MatchTransition(null, ko15, true, ko16, (short) 2));

        // Ko16 → Ko17 (Gewinner), Ko17 (Verlierer)
        // Irgendwie anders evaluieren und setzen. 2. Finale nur wenn Scheibe 1 verloren

        MatchTransition.persist(transitions);

        tournament.setGenerated(true);
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

    public TournamentStatusDto getTournamentStatus(UUID tournamentId) {

        Tournament tournamenet = tournamentRepository.findByIdOptional(tournamentId)
                .orElseThrow(() -> new NotFoundException("Tournament not found"));

        List<StageStatusDto> stageDtos = stageRepository.findByTournamentId(tournamentId).stream()
                .map(stage -> {

                    List<RoundStatusDto> roundDtos = roundRepository.findByStageId(stage.getId()).stream()
                            .map(round -> {

                                List<MatchStatusDto> matchDtos = matchRepository.findByRoundId(round.getId()).stream()
                                        .map(match -> new MatchStatusDto(match.getId(), match.getName(), match.getStatus()))
                                        .toList();

                                return new RoundStatusDto(round.getId(), round.getName(), round.getStatus(), matchDtos);
                            })
                            .toList();

                    return new StageStatusDto(stage.getId(), stage.getName(), stage.getStatus(), roundDtos);
                })
                .toList();

        Status tournamentStatus = deriveTournamentStatus(stageDtos);
        return new TournamentStatusDto(tournamenet.getId(), tournamenet.getName(), tournamentStatus, stageDtos);
    }

    private Status deriveTournamentStatus(List<StageStatusDto> stages) {
        if (stages.isEmpty()) return Status.findById(StatusId.UNKNOWN);

        Set<StatusId> statusIds = stages.stream()
                .map(s -> s.status().getId())
                .collect(Collectors.toSet());

        if (statusIds.contains(StatusId.CANCELED)) return Status.findById(StatusId.CANCELED);
        if (statusIds.contains(StatusId.ONGOING)) return Status.findById(StatusId.ONGOING);
        if (statusIds.contains(StatusId.PAUSED)) return Status.findById(StatusId.PAUSED);

        if (statusIds.stream().allMatch(s -> s == StatusId.ENDED)) return Status.findById(StatusId.ENDED);
        if (statusIds.stream().allMatch(s -> s == StatusId.PLANNED)) return Status.findById(StatusId.PLANNED);

        // Mix aus PLANNED + ENDED → Tournament läuft noch
        return Status.findById(StatusId.ONGOING);
    }
}