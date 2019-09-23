package soya.framework.curly.rest;

import soya.framework.curly.Callback;
import soya.framework.curly.CallbackFactory;

public class RestMethodCallbackFactory implements CallbackFactory<RestMethodInvocation> {
    @Override
    public Callback create(RestMethodInvocation invocation) {
        return new RestMethodCallback(invocation);
    }
}
