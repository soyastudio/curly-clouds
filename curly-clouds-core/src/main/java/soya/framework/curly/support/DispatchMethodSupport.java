package soya.framework.curly.support;

import soya.framework.curly.DispatchMethod;
import soya.framework.commons.reflect.descriptor.MethodDescriptor;
import soya.framework.commons.reflect.descriptor.TypeDescriptor;

import java.lang.reflect.Method;

public abstract class DispatchMethodSupport implements DispatchMethod {
    public static final String DISPATCH = "DISPATCH";

    private final MethodDescriptor method;

    public DispatchMethodSupport(Method method) {
        this.method = fromMethod(method);
    }

    public MethodDescriptor getMethodDescriptor() {
        return method;
    }

    public Method getMethod() {
        return method.getMethod();
    }

    @Override
    public String[] getParameterNames() {
        return method.getParameterNames();
    }

    @Override
    public String dispatchTo() {
        return method.getAnnotation(DISPATCH, DispatchAnnotation.class).dispatchTo;
    }

    @Override
    public String listenTo() {
        return method.getAnnotation(DISPATCH, DispatchAnnotation.class).listenTo;
    }

    public TypeDescriptor getResponseType() {
        return method.getReturnType().getType();
    }

    protected abstract MethodDescriptor fromMethod(Method method);

    public static class DispatchAnnotation {
        private final String dispatchTo;
        private final String listenTo;

        public DispatchAnnotation(String dispatchTo, String listenTo) {
            this.dispatchTo = dispatchTo;
            this.listenTo = listenTo;
        }
    }
}
