package soya.framework.curly.support;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import soya.framework.curly.*;

import java.util.Map;
import java.util.Set;

public abstract class DispatchServiceSupport implements DispatchService, DispatchRegistration {

    private final String name;
    private ImmutableSet<String> schemas;
    private ImmutableMap<String, Operation> processors = ImmutableMap.<String, Operation>builder().build();

    public DispatchServiceSupport() {
        Class<?> c = getClass();
        DispatchContext context = c.getAnnotation(DispatchContext.class);
        while (context == null) {
            if (c.equals(Object.class)) {
                break;
            }
            c = c.getSuperclass();
            context = c.getAnnotation(DispatchContext.class);
        }

        if (context == null) {
            throw new IllegalArgumentException("Cannot find annotation 'DispatchContext' from class: " + getClass().getName());
        }
        this.name = context.name();

        ImmutableSet.Builder<String> builder = ImmutableSet.<String>builder();
        for (String sch : context.schema()) {
            if (!sch.endsWith("://")) {
                builder.add(sch + "://");
            } else {
                builder.add(sch);
            }
        }
        schemas = builder.build();
    }

    public void registerProcessor(String uri, Operation operation) {

    }

    public void registerProcessors(Map<String, Operation> processors) {
        if (processors == null) {
            return;
        }

        ImmutableMap.Builder<String, Operation> builder = ImmutableMap.<String, Operation>builder();
        builder.putAll(this.processors);

        processors.entrySet().forEach(e -> {
            if (match(e.getKey())) {
                builder.put(e.getKey(), e.getValue());
            }
        });

        this.processors = builder.build();
    }

    public boolean match(String uri) {
        for (String s : schemas) {
            if (uri.startsWith(s)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> schemas() {
        return schemas;
    }

    @Override
    public Operation getProcessor(String uri) {
        Operation operation = null;
        if(processors.containsKey(uri)) {
            operation = processors.get(uri);
        } else {
            operation = create(uri);
            processors.put(uri, operation);
        }

        return operation;
    }

    protected static String getSchema(String uri) {
        int index = uri.indexOf("://");
        if (index < 0) {
            throw new IllegalArgumentException("Can not parse uri: " + uri);
        }

        return uri.substring(0, index + 3);
    }
}
