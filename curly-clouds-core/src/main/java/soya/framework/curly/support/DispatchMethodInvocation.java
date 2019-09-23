package soya.framework.curly.support;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import soya.framework.curly.DispatchMethod;
import soya.framework.curly.Invocation;
import soya.framework.curly.JsonCompatible;

import java.lang.reflect.Method;
import java.util.Map;

public abstract class DispatchMethodInvocation implements Invocation, JsonCompatible {

    private final transient DispatchMethod dispatchMethod;
    private final transient Object calller;

    private final ImmutableMap<String, Object> arguments;

    protected DispatchMethodInvocation(DispatchMethod dispatchMethod, Object caller, Map<String, Object> arguments) {
        this.dispatchMethod = dispatchMethod;
        this.calller = caller;
        this.arguments = ImmutableMap.copyOf(arguments);
    }

    @Override
    public Method getMethod() {
        return dispatchMethod.getMethod();
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
