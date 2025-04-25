package com.inspire17.ythelper.validation.annotations;

import com.inspire17.ythelper.validation.FullNameVerification;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FullNameVerification.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FullName {
    String message() default "FullName is not in right format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

