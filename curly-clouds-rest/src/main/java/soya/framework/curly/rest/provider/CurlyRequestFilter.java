package soya.framework.curly.rest.provider;

import soya.framework.curly.rest.CurlyRestContext;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Provider
public class CurlyRequestFilter implements ContainerRequestFilter {
    private static String CURLY_HEADER_PREFIX = "x-curly-";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        DefaultCurlyContext ctx = new DefaultCurlyContext(containerRequestContext);

    }

    static class DefaultCurlyContext extends CurlyRestContext<ContainerRequestContext> {
        protected DefaultCurlyContext(ContainerRequestContext containerRequestContext) {
            super(containerRequestContext);
            MultivaluedMap<String, String> headerMap = containerRequestContext.getHeaders();
            headerMap.entrySet().forEach(en -> {
                String key = en.getKey();
                if (key.startsWith(CURLY_HEADER_PREFIX)) {
                    String propName = key.substring(CURLY_HEADER_PREFIX.length());
                    String propValue = en.getValue().get(0);
                }
            });
        }

        @Override
        protected Map<String, String[]> configure(ContainerRequestContext ctx) {
            Map<String, String[]> results = new HashMap<>();
            MultivaluedMap<String, String> headerMap = ctx.getHeaders();
            headerMap.entrySet().forEach(en -> {
                String key = en.getKey();
                if (key.startsWith(CURLY_HEADER_PREFIX)) {
                    String propName = key.substring(CURLY_HEADER_PREFIX.length());
                    List<String> value = en.getValue();
                    if(value != null && value.size() > 0) {
                        results.put(propName, value.toArray(new String[value.size()]));
                    }
                }
            });

            return results;
        }
    }
}
