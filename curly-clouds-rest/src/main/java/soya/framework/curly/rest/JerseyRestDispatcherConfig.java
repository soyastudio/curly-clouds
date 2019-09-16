package soya.framework.curly.rest;

import javassist.NotFoundException;
import javassist.*;
import org.glassfish.jersey.server.ResourceConfig;
import soya.framework.curly.Dispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

public abstract class JerseyRestDispatcherConfig extends ResourceConfig {

    public JerseyRestDispatcherConfig() {
    }

    public JerseyRestDispatcherConfig(Set<Class<?>> classes) {
        super(classes);
    }

    public JerseyRestDispatcherConfig(Class<?>... classes) {
        super(classes);
    }

    public JerseyRestDispatcherConfig(ResourceConfig original) {
        super(original);
    }

    protected JerseyRestDispatcherConfig registerInterfaces(Class<?>[] intfs, Class<? extends Dispatcher> dispatcherClass) {
        for (Class<?> intf : intfs) {
            if (intf.isInterface()) {
                Class<?> c = generate(intf, dispatcherClass);
                register(c);
            }
        }

        return this;
    }

    private Class<?> generate(Class<?> intf, Class<? extends Dispatcher> dispatcherClass) {
        try {
            String name = intf.getName() + "Dispatcher";

            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.makeClass(name);

            cc.setSuperclass(resolveCtClass(dispatcherClass));

            for (Annotation annotation : intf.getAnnotations()) {

            }

            for (Method method : intf.getDeclaredMethods()) {
                addMethod(cc, method);
            }

            return cc.toClass();

        } catch (CannotCompileException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static CtClass resolveCtClass(Class clazz) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        return pool.get(clazz.getName());
    }

    private static void addMethod(CtClass declaringClass, Method method)
            throws CannotCompileException {

        String id = method.getName();
        String context = "_context";

        /*Dispatch dispatch = method.getAnnotation(Dispatch.class);
        if(dispatch.id().trim().length() > 0) {
            id = dispatch.id();
        }*/

        Parameter[] parameters = method.getParameters();
        MethodParameterDescriptor[] parameterDescriptors = new MethodParameterDescriptor[parameters.length];
        for(int i = 0; i < parameterDescriptors.length; i ++) {
            parameterDescriptors[i] = new MethodParameterDescriptor(parameters[i]);
        }

        for(Parameter parameter: parameters) {
            if(parameter.getAnnotation(Context.class) != null) {
                context = parameter.getName();
                break;
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("public ").append(method.getReturnType().getName()).append(" ")
                .append(method.getName()).append("(");

        for(int i = 0; i < parameterDescriptors.length; i++) {
            if(i > 0) {
                sb.append(", ");
            }
            Parameter p = parameters[i];
            MethodParameterDescriptor methodParameterDescriptor = new MethodParameterDescriptor(p);
            String paramName = p.getName();

            Annotation[] pAnnotations = p.getAnnotations();
            for(Annotation pa: pAnnotations) {
                String paStr = pa.toString();
                if(paStr.contains("value=")) {
                    int start = paStr.indexOf("=");
                    int end = paStr.indexOf(")");
                    paramName = paStr.substring(start + 1, end);
                    paStr = paStr.replace("value=", "\"");
                    paStr = paStr.replace(")", "\")");
                }

                // sb.append(paStr).append(" ");
            }
            sb.append(p.getType().getName()).append(" ").append(paramName);
        }

        sb.append("){").append("return  _dispatch(\"").append(id).append("\", null, ").append(context).append(");}");
        CtMethod ctMethod = CtMethod.make(sb.toString(), declaringClass);



        declaringClass.addMethod(ctMethod);
    }

    private static Class<?> getAnnotationType(Annotation annotation) {
        String s = annotation.toString();
        int index = s.indexOf("(");
        s = s.substring(1, index);
        try {
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static boolean isRestParameter(Class<?> annotationType) {
        return CookieParam.class.equals(annotationType) || FormParam.class.equals(annotationType) || HeaderParam.class.equals(annotationType)
                || PathParam.class.equals(annotationType) || QueryParam.class.equals(annotationType);
    }

    static class MethodParameterDescriptor {
        private String name;
        private Class<?> type;
        private ParameterAnnotationDescriptor[] annotationDescriptors;

        private Parameter parameter;

        public MethodParameterDescriptor(Parameter parameter) {

            this.parameter = parameter;

            this.name = parameter.getName();
            this.type = parameter.getType();

            Annotation[] annotations = parameter.getAnnotations();
            if(annotations != null) {
                annotationDescriptors = new ParameterAnnotationDescriptor[annotations.length];
                for(int i = 0; i < annotations.length; i++) {
                    annotationDescriptors[i] = new ParameterAnnotationDescriptor(annotations[i]);
                    if(isRestParameter(annotationDescriptors[i].getType())) {
                        this.name = annotationDescriptors[i].value;
                    }
                }
            }
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }

        public ParameterAnnotationDescriptor[] getAnnotationDescriptors() {
            return annotationDescriptors;
        }
    }

    static class ParameterAnnotationDescriptor {
        private Class<?> type;
        private String value;

        private boolean restParam;
        private boolean context;

        private Annotation annotation;

        public ParameterAnnotationDescriptor(Annotation annotation) {
            this.annotation = annotation;
            this.type = getAnnotationType(annotation);

            this.restParam = isRestParameter(type);
            this.context = Context.class.equals(type);

            try {
                Method valueMethod = annotation.getClass().getMethod("value", new Class[0]);
                value = (String) valueMethod.invoke(annotation, new Object[0]);

            } catch (Exception e) {

            }

        }

        public Class<?> getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public boolean isRestParam() {
            return restParam;
        }

        public boolean isContext() {
            return context;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder("@");
            builder.append(type.getName());
            if(value != null) {
                builder.append("(\"").append(value).append("\")");
            }

            return builder.toString();
        }
    }
}
