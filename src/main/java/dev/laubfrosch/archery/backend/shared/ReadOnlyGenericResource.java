package dev.laubfrosch.archery.backend.shared;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.ws.rs.*;
import java.util.List;

public abstract class ReadOnlyGenericResource<T extends PanacheEntityBase, I> {

    protected abstract PanacheRepositoryBase<T, I> getRepository();

    protected T findOrThrow(I id) {
        T entity = getRepository().findById(id);
        if (entity == null) {
            throw new NotFoundException("Entity with id " + id + " not found.");
        }
        return entity;
    }

    @GET
    public List<T> getAll() {
        return getRepository().listAll();
    }

    @GET
    @Path("/{id}")
    public T getSingle(@PathParam("id") I id) {
        return findOrThrow(id);
    }
}