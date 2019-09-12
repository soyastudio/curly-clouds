package soya.framework.curly.rest;

import com.google.gson.Gson;
import soya.framework.curly.Session;
import soya.framework.curly.SessionDeserializer;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;

public class RestSessionDeserializer implements SessionDeserializer {

    @Override
    public Object deserialize(Session session) {
        Method method = session.getInvocation().getMethod();
        Gson gson = new Gson();

        if(Response.class.isAssignableFrom(method.getReturnType())) {
            return Response.ok(session.getCurrentState().getAsString()).build();
        }

        return gson.fromJson(session.getCurrentState().getAsString(), method.getGenericReturnType());
    }
}
