package dev.laubfrosch.archery.backend.participant;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class TeamMemberRepository implements PanacheRepositoryBase<TeamMember, UUID> {
}
