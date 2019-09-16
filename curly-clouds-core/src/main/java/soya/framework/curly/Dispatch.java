package soya.framework.curly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Dispatch {
    String uri() default "";

    String[] parameters() default {};

    String listenTo() default "";

 }
