package soya.framework.curly;

import java.lang.reflect.Method;

public interface Invocation extends DataObject {
    String getUri();

    Method getMethod();

    Object getCaller();
}
