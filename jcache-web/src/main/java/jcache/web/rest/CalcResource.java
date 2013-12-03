package jcache.web.rest;

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

import jcache.web.service.CalcService;

@RequestScoped
@Path("calc")
public class CalcResource {
    @Inject
    private CalcService calcService;

    @GET
    @Path("add")
    public String add(@QueryParam("p1") int p1, @QueryParam("p2") int p2) {
        return "Result = " + calcService.add(p1, p2);
    }

    @GET
    @Path("update")
    public void update(@QueryParam("p1") int p1, @QueryParam("p2") int p2) {
        calcService.update(p1, p2, p1 + p2);
    }

    @DELETE
    @Path("delete")
    public void delete(@QueryParam("p1") int p1, @QueryParam("p2") int p2) {
        calcService.remove(p1, p2);
    }

    @DELETE
    @Path("delete-all")
    public void deleteAll() {
        calcService.removeAll();
    }
}
