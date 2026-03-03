package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.participant.Team;
import dev.laubfrosch.archery.backend.participant.TeamService;
import dev.laubfrosch.archery.backend.participant.TeamWithDetailsResponse;
import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
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

    @GET
    @Path("/{id}/teams-with-members")
    @Operation(summary = "Lädt alle Teams eines Turniers inklusive Mitgliedern und Registrierungsdaten")
    @APIResponse(
            responseCode = "200",
            description = "Liste der Teams erfolgreich geladen",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TeamWithDetailsResponse.class, type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY)
            )
    )
    public Response getTeamsWithMembers(@PathParam("id") UUID tournamentId) {
        List<TeamWithDetailsResponse> teams = teamService.getTeamsWithDetails(tournamentId);
        return Response.ok(teams).build();
    }
}