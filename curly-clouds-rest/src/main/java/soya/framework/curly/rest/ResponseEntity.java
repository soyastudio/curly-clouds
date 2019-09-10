package soya.framework.curly.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ResponseEntity {
    Class<?> entityType();

    ContainerType container() default ContainerType.NONE;

    boolean transformable() default false;

    public static enum ContainerType {
        NONE, ARRAY, LIST, SET;
    }

}
