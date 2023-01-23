package com.oakenscience.todoapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = EmailNotExistsValidator.class)
@Documented
public @interface EmailNotExists {
    String message() default "Email already exists";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
