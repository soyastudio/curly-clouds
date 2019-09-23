package soya.framework.curly.rest;

import com.jayway.jsonpath.Filter;
import soya.framework.curly.Session;
import soya.framework.curly.support.CallbackSupport;

public class RestMethodCallback extends CallbackSupport<RestMethodInvocation> {

    private final CurlyRestContext context;

    protected RestMethodCallback(RestMethodInvocation invocation) {
        super(invocation);
        this.context = invocation.getContext();
    }

    @Override
    public void onSuccess(Session session) {
        System.out.println("------------------ callback: " + context);

    }

    @Override
    public boolean onFailure(Session session, Throwable throwable) {
        return false;
    }
}
