package dev.laubfrosch.archery.backend.tournament.creation;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/tournaments/create")
@Tag(name = "Tournament Creation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TournamentCreationResource {

    @Inject
    TournamentCreationService service;

    @POST
    @Operation(summary = "Erstellt ein Turnier mit Teams, Schützen und Registrierungen in einer Transaktion")
    @APIResponse(
            responseCode = "201",
            description = "Turnier erfolgreich erstellt",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TournamentCreateResponse.class)
            )
    )
    @APIResponse(responseCode = "400", description = "Validierungsfehler")
    @APIResponse(responseCode = "409", description = "Teamname bereits vergeben")
    public Response create(@Valid TournamentCreateRequest request) {
        TournamentCreateResponse response = service.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}