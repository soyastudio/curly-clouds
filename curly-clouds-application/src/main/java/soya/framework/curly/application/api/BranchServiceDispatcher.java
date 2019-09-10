package soya.framework.curly.application.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.curly.rest.ResponseEntity;
import soya.framework.curly.rest.RestDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/branch")
@Api(value = "Bank Branch API")
public class BranchServiceDispatcher extends RestDispatcher {

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseEntity(entityType = BankBranch.class, container = ResponseEntity.ContainerType.LIST)
    public Response listAll() {
        return (Response) dispatch("GET://branch/list", new Object[]{});
    }

    @GET
    @Path("/findByCity/{city}")
    @Produces(MediaType.APPLICATION_JSON)
    public BankBranch[] findByCity(@PathParam("city") String city) {
        return null;
    }

    @GET
    @Path("/find/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public BankBranch find(@PathParam("id") String id) {
        return null;
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(BankBranch bankBranch) {
        return null;
    }


    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void remove(@PathParam("id") String id) {

    }
}
