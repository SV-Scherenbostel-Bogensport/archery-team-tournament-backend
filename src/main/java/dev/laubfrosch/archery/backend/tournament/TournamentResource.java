package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.participant.Team;
import dev.laubfrosch.archery.backend.participant.TeamService;
import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/tournaments")
@Tag(name = "Tournament")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TournamentResource extends GenericResource<Tournament, UUID> {

    @Inject
    TournamentRepository repository;

    @Inject
    TournamentService tournamentService;

    @Inject
    TeamService teamService;

    @Override
    protected PanacheRepositoryBase<Tournament, UUID> getRepository() {
        return repository;
    }

    @GET
    @Path("/overview")
    public List<TournamentOverviewResponse> getOverview() {
        return tournamentService.getOverview();
    }

    @GET
    @Path("/{id}/teams")
    public List<Team> getTournamentTeams(@PathParam("id") UUID id) {
        findOrThrow(id);
        return teamService.getTeamsByTournament(id);
    }
}