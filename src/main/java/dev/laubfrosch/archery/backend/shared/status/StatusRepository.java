package dev.laubfrosch.archery.backend.shared.status;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StatusRepository implements PanacheRepositoryBase<Status, StatusId> {
}