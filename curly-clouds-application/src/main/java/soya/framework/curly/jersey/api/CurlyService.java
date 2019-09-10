package soya.framework.curly.jersey.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.curly.Dispatch;
import soya.framework.curly.DispatchMethod;
import soya.framework.curly.DispatchRegistration;
import soya.framework.curly.rest.RestDispatcher;
import soya.framework.curly.support.DispatchServiceSingleton;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Path("/curly")
@Api(value = "Curly Service")
public class CurlyService extends RestDispatcher {

    @GET
    @Path("/index")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getMsg() {
        System.out.println("------- Dispatch Service: " + DispatchServiceSingleton.getInstance());
        return Response.status(200).entity("Hello World").build();

    }

    @GET
    @Path("/schemas")
    @Consumes("application/json")
    @Produces("application/json")
    public Response schemas() {
        DispatchRegistration registration = (DispatchRegistration) DispatchServiceSingleton.getInstance();
        List<String> list = new ArrayList<>(registration.schemas());
        Collections.sort(list);
        return Response.status(200).entity(list).build();

    }

    @GET
    @Path("/available")
    @Consumes("application/json")
    @Produces("application/json")
    public Response available() {
        DispatchRegistration registration = (DispatchRegistration) DispatchServiceSingleton.getInstance();
        return Response.status(200).entity(registration.available()).build();

    }

    @GET
    @Path("/available/{schema}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response available(@PathParam("schema") String schema) {
        String token = schema + "://";
        DispatchRegistration registration = (DispatchRegistration) DispatchServiceSingleton.getInstance();
        List<String> list = new ArrayList<>();
        registration.available().forEach(e -> {
            if(e.startsWith(token)) {
                list.add(e);
            }
        });

        Collections.sort(list);

        return Response.status(200).entity(list).build();

    }

    @GET
    @Path("/prepare")
    @Consumes("application/json")
    @Produces("application/json")
    public Response prepare(@HeaderParam("uri") String uri) {
        DispatchRegistration registration = (DispatchRegistration) DispatchServiceSingleton.getInstance();
        DispatchMethod dispatchMethod = registration.getDispatchMethod(uri);

        return Response.status(200).entity(dispatchMethod.getParameterNames()).build();

    }
}
