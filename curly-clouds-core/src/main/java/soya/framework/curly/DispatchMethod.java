package soya.framework.curly;

import java.lang.reflect.Method;

public interface DispatchMethod {
    String getUri();

    Method getMethod();

    String[] getParameterNames();

    Invocation createInvocation(Object caller, Object[] arguments) throws IllegalArgumentException;
}
