package dev.laubfrosch.archery.backend.tournament;

import dev.laubfrosch.archery.backend.api.dto.TournamentCreateRequest;
import dev.laubfrosch.archery.backend.api.dto.TournamentCreateResponse;
import dev.laubfrosch.archery.backend.api.dto.TournamentOverviewResponse;
import dev.laubfrosch.archery.backend.participant.TeamService;
import dev.laubfrosch.archery.backend.api.dto.TeamWithDetailsDto;
import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
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
    @Operation(summary = "Retrieves an overview of all tournaments")
    @APIResponse(
            responseCode = "200",
            description = "List of tournaments successfully loaded",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = TournamentOverviewResponse.class,
                            type = SchemaType.ARRAY
                    )
            )
    )
    public List<TournamentOverviewResponse> getOverview() {
        return tournamentService.getOverview();
    }

    @POST
    @Path("/create")
    @Operation(summary = "Creates a tournament with teams, members and registrations")
    @APIResponse(
            responseCode = "201",
            description = "Tournament successfully created",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TournamentCreateResponse.class)
            )
    )
    @APIResponse(responseCode = "400", description = "Validation error")
    @APIResponse(responseCode = "409", description = "The same team names were assigned multiple times")
    public Response create(@Valid TournamentCreateRequest request) {
        TournamentCreateResponse response = tournamentService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}/teams")
    @Operation(summary = "Loads all teams in a tournament, including members and registration details")
    @APIResponse(
            responseCode = "200",
            description = "List of teams successfully loaded",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TeamWithDetailsDto.class, type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY)
            )
    )
    public Response getTeamsWithMembers(@PathParam("id") UUID tournamentId) {
        List<TeamWithDetailsDto> teams = teamService.getTeamsWithDetails(tournamentId);
        return Response.ok(teams).build();
    }
}
