package soya.framework.curly.rest;


import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import soya.framework.curly.Invocation;
import soya.framework.curly.JsonCompatible;

import java.lang.reflect.Method;
import java.util.Map;

public final class RestMethodInvocation implements Invocation, JsonCompatible {
    private final transient RestDispatchMethod restMethod;
    private final transient Object calller;
    private final transient String uri;

    private final ImmutableMap<String, Object> arguments;

    RestMethodInvocation(RestDispatchMethod restMethod, Object caller, Map<String, Object> arguments) {
        this.uri = restMethod.getUri();

        this.restMethod = restMethod;
        this.calller = caller;
        this.arguments = ImmutableMap.copyOf(arguments);
    }

    public String getUri() {
        return uri;
    }

    @Override
    public Method getMethod() {
        return restMethod.getMethod();
    }

    @Override
    public Object getCaller() {
        return calller;
    }

    @Override
    public String getAsString() {
        return getAsJsonString();
    }

    @Override
    public String getAsJsonString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(arguments);
    }
}

