package soya.framework.curly.support;

import soya.framework.curly.Dispatch;
import soya.framework.curly.DispatchMethod;
import soya.framework.curly.Invocation;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public final class DirectDispatchMethod implements DispatchMethod {

    private final transient Method method;

    private final String uri;
    private final String[] parameterNames;

    private String dispatchTo;
    private String listenTo;

    private DirectDispatchMethod(Method method, String uri, String[] parameterNames) {
        this.method = method;
        this.uri = uri;
        this.parameterNames = parameterNames;

        Dispatch dispatch = method.getAnnotation(Dispatch.class);
        if(dispatch != null) {
            this.dispatchTo = dispatch.uri();
            this.listenTo = dispatch.listenTo();
        }
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String[] getParameterNames() {
        return getParameterNames();
    }

    @Override
    public String dispatchTo() {
        return dispatchTo;
    }

    @Override
    public String listenTo() {
        return listenTo;
    }

    @Override
    public Invocation createInvocation(Object caller, Object[] arguments) throws IllegalArgumentException {
        return null;
    }

    public static DirectDispatchMethod fromMethod(Method method) {
        String uri = null;
        String[] paramNames = null;
        Dispatch dispatch = method.getAnnotation(Dispatch.class);
        if (dispatch == null) {
            uri = getDefaultUri(method);
            paramNames =getDefaultParameterNames(method);

        } else {
            uri = dispatch.uri() == "" ? getDefaultUri(method) : dispatch.uri();
            paramNames = dispatch.parameters().length == 0 ? getDefaultParameterNames(method) : dispatch.parameters();
        }

        return new DirectDispatchMethod(method, uri, paramNames);
    }

    private static String getDefaultUri(Method method) {
        return new StringBuilder("direct://")
                .append(method.getDeclaringClass()
                        .getSimpleName())
                .append("/")
                .append(method.getName())
                .toString();
    }

    private static String[] getDefaultParameterNames(Method method) {
        Parameter[] parameters = method.getParameters();
        String[] names = new String[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            names[i] = parameters[i].getName();
        }

        return names;
    }
}
