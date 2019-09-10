package soya.framework.curly.support;

import com.google.gson.*;
import soya.framework.curly.Invocation;
import soya.framework.curly.util.GsonUtils;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class DispatchMethodInvocation implements Invocation, GsonCompatible {

    private Object[] arguments;
    private Object localObject;

    private Gson gson;
    private JsonObject root;

    private DispatchMethodInvocation(Object[] arguments, Object localObject) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, jsonSerializationContext)
                -> new JsonPrimitive(date.toString()));
        gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return aClass.equals(Class.class);
            }
        });
        gson = gsonBuilder.create();

        this.arguments = arguments;
        this.localObject = localObject;

        root = new JsonObject();
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] != null) {
                JsonElement e = gson.toJsonTree(arguments[i]);
                root.add("p" + i, e);
            }
        }

        if (localObject != null) {
            root.add("localObject", gson.toJsonTree(localObject));
        }

    }

    public Object[] getArguments() {
        return arguments;
    }

    public Object getLocalObject() {
        return localObject;
    }

    public JsonElement getAsJsonElement() {
        return root;
    }

    @Override
    public String getAsJsonString() {
        return root.toString();
    }

    @Override
    public String getAsString() {
        return gson.toJson(root);
    }

    public Map<String, Object> getAsMap() {
        return GsonUtils.toMap(root);
    }

    public static MethodDispatchInputDataBuilder builder(Object[] arguments) {
        return new MethodDispatchInputDataBuilder(arguments);
    }

    @Override
    public String getUri() {
        return null;
    }

    @Override
    public Method getMethod() {
        return null;
    }

    @Override
    public Object getCaller() {
        return null;
    }

    public static class MethodDispatchInputDataBuilder {
        private Object[] arguments;
        private Map<String, Object> attributes;

        private MethodDispatchInputDataBuilder(Object[] arguments) {
            this.arguments = arguments == null ? new Object[0] : arguments;
            this.attributes = new LinkedHashMap<>();
        }

        public MethodDispatchInputDataBuilder addAttribute(Object obj) {
            attributes.put(obj.getClass().getSimpleName(), obj);
            return this;
        }

        public DispatchMethodInvocation create() {
            return new DispatchMethodInvocation(arguments, attributes);
        }
    }


}
