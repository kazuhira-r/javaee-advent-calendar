package javaeeadventcalendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("book")
public class BookResource {
    @GET
    @Path("{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book find(@PathParam("isbn") String isbn) {
        return EntityManagerProvider.call(em -> em.find(Book.class, isbn));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Book book, @Context UriInfo uriInfo) {
        EntityManagerProvider.run(em -> {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(book);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();

                throw new RuntimeException(e);
            }
        });

        return Response.created(uriInfo.getRequestUriBuilder().path(book.getIsbn()).build()).build();
    }
}
