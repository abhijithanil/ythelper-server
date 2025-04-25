package com.inspire17.ythelper.validation.annotations;

import com.inspire17.ythelper.validation.EmailVerification;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailVerification.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    String message() default "Email is not in right format or is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
