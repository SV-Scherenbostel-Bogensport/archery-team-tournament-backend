package dev.laubfrosch.archery.backend.participant;

import dev.laubfrosch.archery.backend.shared.GenericResource;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/team-members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeamMemberResource extends GenericResource<TeamMember, UUID> {

    @Inject
    TeamMemberRepository repository;

    @Override
    protected PanacheRepositoryBase<TeamMember, UUID> getRepository() {
        return repository;
    }
}
