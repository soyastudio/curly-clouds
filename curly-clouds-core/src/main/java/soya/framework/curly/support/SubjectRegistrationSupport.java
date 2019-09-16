package soya.framework.curly.support;

import com.google.common.collect.ImmutableMap;
import soya.framework.curly.DispatchMethod;
import soya.framework.curly.Dispatcher;
import soya.framework.curly.SubjectRegistration;

import java.lang.reflect.Method;
import java.util.*;

public abstract class SubjectRegistrationSupport<T extends DispatchMethod> implements SubjectRegistration {
    protected ImmutableMap<String, T> methods = ImmutableMap.<String, T>builder().build();

    @Override
    public DispatchMethod[] dispatchMethods() {
        return methods.values().toArray(new DispatchMethod[methods.size()]);
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
    public void registerSubjects(Class<?>[] subjects) {
        Set<T> set = new HashSet<>();

        for(Class<?> cls: subjects) {
            if(Dispatcher.class.isAssignableFrom(cls)) {
               for(Method method: cls.getDeclaredMethods()) {
                   T dispatchMethod = fromMethod(method);
                   if(dispatchMethod != null) {
                       set.add(dispatchMethod);
                   }
               }
            }
        }

        registerDispatchMethods(set);
    }

    protected void registerDispatchMethods(Collection<T> dispatchMethods) {
        Map<String, T> map = new HashMap(methods);
        for(T t: dispatchMethods) {
            map.put(t.getUri(), t);
        }
        this.methods = ImmutableMap.copyOf(map);

    }

    protected abstract T fromMethod(Method method);
}
