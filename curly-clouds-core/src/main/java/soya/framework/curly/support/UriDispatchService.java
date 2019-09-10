package soya.framework.curly.support;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import soya.framework.curly.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class UriDispatchService implements DispatchService, DispatchRegistration {
    private ImmutableSet<String> schemas;
    private ImmutableMap<String, ? extends DispatchMethod> methods = ImmutableMap.<String, DispatchMethod>builder().build();
    private ImmutableMap<String, ? extends Operation> processors = ImmutableMap.<String, Operation>builder().build();

    public UriDispatchService() {
        Class<?> c = getClass();
        DispatchContext context = c.getAnnotation(DispatchContext.class);
        while (context == null) {
            if(c.equals(Object.class)) {
                break;
            }
            c = c.getSuperclass();
            context = c.getAnnotation(DispatchContext.class);
        }

        if(context == null) {
            throw new IllegalArgumentException("Cannot find annotation 'DispatchContext' from class: " + getClass().getName());
        }

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

    protected void registerMethods(Collection<? extends DispatchMethod> methods) {
        if(methods == null) {
            return;
        }

        ImmutableMap.Builder<String, DispatchMethod> builder = ImmutableMap.<String, DispatchMethod>builder();
        builder.putAll(this.methods);
        methods.forEach(m -> {
            builder.put(m.getUri(), m);
        });

        this.methods = builder.build();
    }

    public void registerProcessors(Map<String, ? extends Operation> processors) {
        if (processors == null) {
            return;
        }

        ImmutableMap.Builder<String, Operation> builder = ImmutableMap.<String, Operation>builder();
        builder.putAll(this.processors);

        processors.entrySet().forEach(e -> {
            if(match(e.getKey())) {
                builder.put(e.getKey(), e.getValue());
            }
        });

        this.processors = builder.build();
    }

    public boolean match(String uri) {
        for(String s: schemas) {
            if(uri.startsWith(s)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<String> schemas() {
        return schemas;
    }

    @Override
    public Set<String> available() {
        return methods.keySet();
    }

    @Override
    public boolean contains(String uri) {
        return methods.containsKey(uri);
    }

    @Override
    public DispatchMethod getDispatchMethod(String uri) {
        return methods.get(uri);
    }

    @Override
    public Operation getProcessor(String uri) {
        return processors.get(uri);
    }

    public abstract void registerSubjects(Class<?>[] subjects);
}
