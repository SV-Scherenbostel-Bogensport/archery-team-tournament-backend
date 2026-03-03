package dev.laubfrosch.archery.backend.participant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TeamService {

    @Inject
    TeamRepository teamRepository;

    public List<Team> getTeamsByTournament(UUID tournamentId) {
        return teamRepository.list("tournament.id", tournamentId);
    }
}
