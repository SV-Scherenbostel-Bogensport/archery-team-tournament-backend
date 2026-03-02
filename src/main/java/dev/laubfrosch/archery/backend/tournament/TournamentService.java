package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.participant.Team;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class TournamentService {

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<TournamentOverviewResponse> getOverview() {
        return Tournament.<Tournament>listAll().stream().map(t -> {
            TournamentOverviewResponse dto = new TournamentOverviewResponse();
            dto.setId(t.getId());
            dto.setName(t.getName());
            dto.setLocation(t.getLocation());
            dto.setDate(t.getDate());
            dto.setStartTime(t.getStartTime());
            dto.setAllowRegistration(t.getAllowRegistration());
            dto.setMaxSlots(t.getMaxSlots() != null ? t.getMaxSlots().intValue() : null);
            dto.setGenerated(t.getGenerated());
            dto.setTeamCount((int) Team.count("tournament = ?1", t));
            return dto;
        }).toList();
    }
}