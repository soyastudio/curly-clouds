package soya.framework.curly.rest;

import com.google.gson.Gson;
import soya.framework.curly.DispatchContext;
import soya.framework.curly.DispatchException;
import soya.framework.curly.support.UriDispatchService;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@DispatchContext(schema = {
        "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"
})
public final class RestDispatchService extends UriDispatchService {

    public RestDispatchService() {
        super();
    }

    @Override
    public void registerSubjects(Class<?>[] subjects) {
        Set<RestDispatchMethod> set = new HashSet<>();
        for (Class<?> c : subjects) {
            if (RestDispatcher.class.isAssignableFrom(c)) {
                String base = "";
                if (c.getAnnotation(Path.class) != null) {
                    base = c.getAnnotation(Path.class).value();
                }
                if (base.startsWith("/")) {
                    base = base.substring(1);
                }
                if (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }
                Method[] methods = c.getDeclaredMethods();
                for (Method method : methods) {
                    RestDispatchMethod restMethod = RestDispatchMethod.fromMethod(method);
                    set.add(restMethod);
                }
            }
        }

        this.registerMethods(set);

    }

    @Override
    public Object dispatch(Object caller, String uri, Object[] args) throws DispatchException {
        if (!contains(uri)) {
            throw new DispatchException("Rest method is not defined for uri: " + uri);
        }

        RestDispatchMethod restMethod = (RestDispatchMethod) getDispatchMethod(uri);
        RestOperation processor = (RestOperation) getProcessor(uri);

        if (processor == null) {
            throw new DispatchException("Rest processor is not defined for uri: " + uri);
        }
        RestProcessSession session = new RestProcessSession(restMethod.createInvocation(caller, args));
        processor.process(session);

        Gson gson = new Gson();

        if(Response.class.isAssignableFrom(restMethod.getMethod().getReturnType())) {
            return Response.ok(session.getCurrentState().getAsString()).build();
        }

        return gson.fromJson(session.getCurrentState().getAsString(), restMethod.getMethod().getGenericReturnType());
    }
}
