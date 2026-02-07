package dev.laubfrosch.archery.backend.tournament;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StatusRepository implements PanacheRepositoryBase<Status, Integer> {
}
