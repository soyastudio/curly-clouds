package soya.framework.curly.rest;

import soya.framework.curly.support.SubjectRegistrationSupport;

import javax.ws.rs.Path;
import java.lang.reflect.Method;

public class RestSubjectRegistration extends SubjectRegistrationSupport<RestDispatchMethod> {

    @Override
    protected RestDispatchMethod fromMethod(Method method) {
        if(method.getAnnotation(Path.class) == null) {
            return null;
        }

        return RestDispatchMethod.fromMethod(method);
    }
}
