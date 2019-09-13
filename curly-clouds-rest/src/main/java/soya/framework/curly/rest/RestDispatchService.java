package soya.framework.curly.rest;

import soya.framework.curly.DispatchContext;
import soya.framework.curly.SessionDeserializer;
import soya.framework.curly.support.DispatchServiceSupport;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@DispatchContext(name = "REST Dispatch Service", schema = {
        "GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"
})
public final class RestDispatchService extends DispatchServiceSupport {

    public RestDispatchService() {
        this(new RestSessionDeserializer());
    }

    public RestDispatchService(SessionDeserializer deserializer) {
        super(deserializer);
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
}
