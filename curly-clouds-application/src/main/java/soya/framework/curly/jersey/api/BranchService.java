package soya.framework.curly.jersey.api;

import io.swagger.annotations.Api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//@Path("/branch")
//@Api(value = "Bank Branch API")
public interface BranchService {

    //@GET
    //@Path("/")
    //@Produces(MediaType.APPLICATION_JSON)
    Response listAll();

    //@GET
    //@Path("/find")
    //@Produces(MediaType.APPLICATION_JSON)
    Response findByCity(@QueryParam("city") String city);



}
