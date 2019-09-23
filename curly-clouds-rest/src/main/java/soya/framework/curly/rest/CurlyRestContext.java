package soya.framework.curly.rest;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public abstract class CurlyRestContext<E> {
    private static ThreadLocal<CurlyRestContext> instance = new ThreadLocal<CurlyRestContext>() {
        protected CurlyRestContext initialValue() {
            return null;
        }
    };

    private ImmutableMap<String, String[]> properties;

    protected CurlyRestContext(E ctx) {
        properties = ImmutableMap.copyOf(configure(ctx));
        setCurrentInstance(this);
    }

    protected abstract Map<String, String[]> configure(E ctx);

    public String getFirst(String key) {
        if(properties.containsKey(key) && properties.get(key).length > 0) {
            return properties.get(key)[0];
        } else {
            return null;
        }
    }

    public String[] get(String key) {
        return properties.get(key);
    }

    public <T> T extract(CurlyRestContextExtractor<T> extractor) {
        return extractor.extract(this);
    }

    protected static void setCurrentInstance(CurlyRestContext context) {
        if (context == null) {
            instance.remove();
        } else {
            instance.set(context);
        }

    }

    public static CurlyRestContext getCurrentContext() {
        CurlyRestContext ctx = (CurlyRestContext) instance.get();
        return ctx;
    }

}
