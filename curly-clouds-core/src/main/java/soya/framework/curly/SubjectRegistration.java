package soya.framework.curly;

import java.lang.reflect.Method;
import java.util.Set;

public interface SubjectRegistration {
    void registerSubjects(Class<?>[] subjects);

    DispatchMethod[] dispatchMethods();

    Set<String> available();

    boolean contains(String uri);
}
