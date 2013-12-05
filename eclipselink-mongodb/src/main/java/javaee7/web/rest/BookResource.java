package javaee7.web.rest;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javaee7.web.entity.Book;
import javaee7.web.service.BookService;

@RequestScoped
@Path("books")
public class BookResource {
    @Inject
    private BookService bookService;

    @Path("{isbn}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Book book(@PathParam("isbn") String isbn, @QueryParam("native") String nativeQuery) {
        if (Boolean.valueOf(nativeQuery)) {
            return bookService.findNative(isbn);
        } else {
            return bookService.find(isbn);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book create(Book book) {
        bookService.create(book);
        return book;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(Book book) {
        bookService.update(book);
    }

    @Path("findAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> findAll() {
        return bookService.findAll();
    }

    @Path("find")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> find(@QueryParam("price") int price) {
        return bookService.findByPrice(price);
    }

    @Path("{isbn}")
    @DELETE
    public void delete(@PathParam("isbn") String isbn) {
        Book book = bookService.find(isbn);
        if (book != null) {
            bookService.remove(book);
        }
    }
}