package soya.framework.curly.rest;

import soya.framework.curly.Callback;
import soya.framework.curly.Session;
import soya.framework.curly.support.DispatchMethodInvocation;

import java.util.Map;

public final class RestMethodInvocation extends DispatchMethodInvocation {
    private final transient String uri;
    private final transient CurlyRestContext context;

    protected RestMethodInvocation(RestDispatchMethod restMethod, Object caller, Map<String, Object> arguments, CurlyRestContext context) {
        super(restMethod, caller, arguments);
        this.uri = restMethod.getUri();
        this.context = context;

    }

    public String getUri() {
        return uri;
    }

    public CurlyRestContext getContext() {
        return context;
    }
}

