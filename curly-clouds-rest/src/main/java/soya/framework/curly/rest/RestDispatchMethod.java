package soya.framework.curly.rest;

import com.google.common.collect.ImmutableList;
import soya.framework.curly.Dispatch;
import soya.framework.curly.DispatchMethod;

import javax.annotation.Nonnull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class RestDispatchMethod implements DispatchMethod {

    private final Method method;
    private final String[] parameterNames;
    private final String uri;
    private final HttpMethod httpMethod;
    private final ImmutableList<RestParameter> parameters;
    private ResponseEntityType responseEntityType = new ResponseEntityType();
    private String dispatchTo;
    private String listenTo;

    private RestDispatchMethod(Method method) throws RestMethodException {
        this.method = method;
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

        this.uri = httpMethod + "://" + base + path;

        List<RestParameter> params = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        for (Parameter param : method.getParameters()) {
            RestParameter rp = new RestParameter(param);
            params.add(rp);
            paramNames.add(rp.getName());
        }
        this.parameters = ImmutableList.copyOf(params);
        this.parameterNames = paramNames.toArray(new String[paramNames.size()]);

        // response entity type
        Class<?> returnType = method.getReturnType();
        if (returnType.getName().equals("void")) {
            responseEntityType.entityType = Void.class;

        } else if (returnType.isArray()) {
            responseEntityType.entityType = returnType.getComponentType();
            responseEntityType.container = "ARRAY";

        } else if (Collection.class.isAssignableFrom(returnType)) {
            Type t = method.getGenericReturnType();
            try {
                Type type = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                responseEntityType.entityType = Class.forName(type.getTypeName());
                if (List.class.isAssignableFrom(returnType)) {
                    responseEntityType.container = "LIST";
                } else if (Set.class.isAssignableFrom(returnType)) {
                    responseEntityType.container = "SET";
                } else {
                    responseEntityType.container = "LIST";
                }

            } catch (Exception e) {
                throw new RestMethodException("Cannot determine response entity type.");
            }
        } else if (Response.class.isAssignableFrom(returnType)) {
            if (method.getAnnotation(ResponseEntity.class) != null) {
                ResponseEntity annotation = method.getAnnotation(ResponseEntity.class);
                responseEntityType.entityType = annotation.entityType();
                responseEntityType.container = annotation.container().equals(ResponseEntity.ContainerType.NONE) ? null : annotation.container().toString();
                responseEntityType.transformable = annotation.transformable();

            } else {

            }
        } else {
            responseEntityType.entityType = returnType;
        }

        Dispatch dispatchAnnotation = method.getAnnotation(Dispatch.class);
        if(dispatchAnnotation != null) {
            this.dispatchTo = dispatchAnnotation.uri();
            this.listenTo = dispatchAnnotation.listenTo();
        }
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String[] getParameterNames() {
        return parameterNames;
    }

    @Override
    public String dispatchTo() {
        return dispatchTo;
    }

    @Override
    public String listenTo() {
        return listenTo;
    }

    public ResponseEntityType getResponseEntityType() {
        return responseEntityType;
    }

    public RestMethodInvocation createInvocation(Object caller, Object[] arguments) throws IllegalArgumentException {
        Objects.requireNonNull(arguments, "arguments == null");
        if (this.parameters.size() != arguments.length) {
            throw new IllegalArgumentException();
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < arguments.length; i++) {
            RestParameter rp = parameters.get(i);
            Object arg = arguments[i];
            if (!rp.getType().isInstance(arg)) {

            }

            if (arg == null && !rp.isNullable()) {

            }

            params.put(rp.getName(), arg);
        }

        return new RestMethodInvocation(this, caller, params);
    }

    public static RestDispatchMethod fromMethod(Method method) {
        return new RestDispatchMethod(method);
    }

    public static class RestParameter {
        private final String name;
        private final Class<?> type;
        private final ParameterType parameterType;
        private final boolean nullable;

        public RestParameter(Parameter parameter) {
            this.type = parameter.getType();
            if (parameter.getAnnotation(CookieParam.class) != null) {
                this.name = parameter.getAnnotation(CookieParam.class).value();
                this.parameterType = ParameterType.COOKIE;

            } else if (parameter.getAnnotation(HeaderParam.class) != null) {
                this.name = parameter.getAnnotation(HeaderParam.class).value();
                this.parameterType = ParameterType.HEADER;

            } else if (parameter.getAnnotation(MatrixParam.class) != null) {
                this.name = parameter.getAnnotation(MatrixParam.class).value();
                this.parameterType = ParameterType.MATRIX;

            } else if (parameter.getAnnotation(PathParam.class) != null) {
                this.name = parameter.getAnnotation(PathParam.class).value();
                this.parameterType = ParameterType.PATH;

            } else if (parameter.getAnnotation(QueryParam.class) != null) {
                this.name = parameter.getAnnotation(QueryParam.class).value();
                this.parameterType = ParameterType.QUERY;

            } else {
                this.name = "body";
                this.parameterType = ParameterType.BODY;
            }

            if (parameter.getAnnotation(Nonnull.class) != null) {
                this.nullable = false;
            } else {
                this.nullable = true;
            }
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public ParameterType getParameterType() {
            return parameterType;
        }

        public boolean isNullable() {
            return nullable;
        }
    }

    public static class ResponseEntityType {
        private Class<?> entityType;
        private String container;
        private boolean transformable;

        public Class<?> getEntityType() {
            return entityType;
        }

        public String getContainer() {
            return container;
        }

        public boolean isTransformable() {
            return transformable;
        }
    }

    public static enum HttpMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS;
    }

    public static enum ParameterType {
        BODY, COOKIE, FORM, HEADER, MATRIX, PATH, QUERY;
    }
}
