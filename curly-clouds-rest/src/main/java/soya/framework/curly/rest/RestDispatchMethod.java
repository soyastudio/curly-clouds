package soya.framework.curly.rest;

import soya.framework.curly.Dispatch;
import soya.framework.commons.reflect.descriptor.MethodDescriptor;
import soya.framework.commons.reflect.descriptor.MethodParameterDescriptor;
import soya.framework.curly.support.DispatchMethodSupport;

import javax.ws.rs.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RestDispatchMethod extends DispatchMethodSupport {
    public static final String REST_METHOD = "REST-METHOD";
    public static final String REST_PARAM_TYPE = "REST-PARAM";
    public static final String REST_BODY_PARAM_TYPE = "body";

    public RestDispatchMethod(Method method) {
        super(method);
    }

    @Override
    protected MethodDescriptor fromMethod(Method method) {
        MethodDescriptor.MethodDescriptorBuilder builder = MethodDescriptor.builder(method);

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(CookieParam.class) != null) {
                builder.setParameterName(i, parameters[i].getAnnotation(CookieParam.class).value());
                builder.annotateParameter(i, REST_PARAM_TYPE, ParameterType.COOKIE);

            } else if (parameters[i].getAnnotation(HeaderParam.class) != null) {
                builder.setParameterName(i, parameters[i].getAnnotation(HeaderParam.class).value());
                builder.annotateParameter(i, REST_PARAM_TYPE, ParameterType.HEADER);

            } else if (parameters[i].getAnnotation(MatrixParam.class) != null) {
                builder.setParameterName(i, parameters[i].getAnnotation(MatrixParam.class).value());
                builder.annotateParameter(i, REST_PARAM_TYPE, ParameterType.MATRIX);

            } else if (parameters[i].getAnnotation(PathParam.class) != null) {
                builder.setParameterName(i, parameters[i].getAnnotation(PathParam.class).value());
                builder.annotateParameter(i, REST_PARAM_TYPE, ParameterType.PATH);

            } else if (parameters[i].getAnnotation(QueryParam.class) != null) {
                builder.setParameterName(i, parameters[i].getAnnotation(QueryParam.class).value());
                builder.annotateParameter(i, REST_PARAM_TYPE, ParameterType.QUERY);

            } else {
                builder.setParameterName(i, REST_BODY_PARAM_TYPE);
                builder.annotateParameter(i, REST_PARAM_TYPE, ParameterType.BODY);
            }
        }

        Class<?> declareClass = method.getDeclaringClass();
        String base = "";
        if (declareClass.getAnnotation(Path.class) != null) {
            base = declareClass.getAnnotation(Path.class).value();
        }
        if (base.startsWith("/")) {
            base = base.substring(1);
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        String path = "/";
        if (method.getAnnotation(Path.class) != null) {
            path = method.getAnnotation(Path.class).value();
        }

        HttpMethod httpMethod = null;
        if (method.getAnnotation(POST.class) != null) {
            httpMethod = HttpMethod.POST;

        } else if (method.getAnnotation(DELETE.class) != null) {
            httpMethod = HttpMethod.DELETE;

        } else if (method.getAnnotation(PUT.class) != null) {
            httpMethod = HttpMethod.PUT;

        } else if (method.getAnnotation(HEAD.class) != null) {
            httpMethod = HttpMethod.HEAD;

        } else if (method.getAnnotation(OPTIONS.class) != null) {
            httpMethod = HttpMethod.OPTIONS;

        } else {
            httpMethod = HttpMethod.GET;

        }
        path = base + path;

        builder.annotate(REST_METHOD, new RestMethodAnnotation(path, httpMethod));

        // dispatch:
        Dispatch dispatch = method.getAnnotation(Dispatch.class);
        if (dispatch != null) {
            builder.annotate(DISPATCH, new DispatchAnnotation(dispatch.uri(), dispatch.subscribe()));
        }

        return builder.build();
    }

    public String getUri() {
        return getMethodDescriptor().getAnnotation(REST_METHOD, RestMethodAnnotation.class).getUri();
    }


    public RestMethodInvocation createInvocation(Object caller, Object[] arguments) throws IllegalArgumentException {
        Objects.requireNonNull(arguments, "arguments == null");
        if (getMethodDescriptor().getParameters().size() != arguments.length) {
            throw new IllegalArgumentException();
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < arguments.length; i++) {
            MethodParameterDescriptor rp = getMethodDescriptor().getParameters().get(i);
            Object arg = arguments[i];
            if (!rp.getType().isInstance(arg)) {

            }

            params.put(rp.getName(), arg);
        }

        CurlyRestContext curlyRestContext = CurlyRestContext.getCurrentContext();
        return new RestMethodInvocation(this, caller, params, curlyRestContext);
    }

    public static enum HttpMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS;
    }

    public static enum ParameterType {
        BODY, COOKIE, FORM, HEADER, MATRIX, PATH, QUERY;
    }

    public static class RestMethodAnnotation {
        private final String path;
        private final HttpMethod httpMethod;

        private RestMethodAnnotation(String path, HttpMethod httpMethod) {
            this.path = path.startsWith("/") ? path : "/" + path;
            this.httpMethod = httpMethod;
        }

        public String getPath() {
            return path;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public String getUri() {
            return httpMethod + ":/" + path;
        }
    }
}
