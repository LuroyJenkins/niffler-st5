package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.model.UserState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static guru.qa.niffler.model.UserState.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface User {

    UserState value() default COMMON;

}
