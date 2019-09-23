package soya.framework.curly.support;

import soya.framework.curly.Callback;
import soya.framework.curly.Invocation;

public abstract class CallbackSupport<T extends Invocation> implements Callback {
    private final T invocation;

    protected CallbackSupport(T invocation) {
        this.invocation = invocation;
    }

    public T getInvocation() {
        return invocation;
    }
}
