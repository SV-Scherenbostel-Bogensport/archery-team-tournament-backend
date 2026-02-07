package dev.laubfrosch.archery.backend.shared;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

public abstract class GenericResource<T extends PanacheEntityBase, I> {

    protected abstract PanacheRepositoryBase<T, I> getRepository();

    @GET
    public List<T> getAll() {
        return getRepository().listAll();
    }

    @GET
    @Path("/{id}")
    public T getSingle(@PathParam("id") I id) {
        T entity = getRepository().findById(id);
        if (entity == null) {
            throw new WebApplicationException("Entity with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(@Valid T entity) {
        getRepository().persist(entity);
        return Response.status(201).entity(entity).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public T update(@PathParam("id") I id, @Valid T entity) {
        T existing = getRepository().findById(id);
        if (existing == null) {
            throw new WebApplicationException("Entity with id " + id + " not found.", 404);
        }
        return getRepository().getEntityManager().merge(entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") I id) {
        boolean deleted = getRepository().deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(404).build();
    }

}
