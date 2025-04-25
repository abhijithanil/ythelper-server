package com.inspire17.ythelper.validation.annotations;

import com.inspire17.ythelper.validation.UserNameVerification;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserNameVerification.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {
    String message() default "UserName is not in right format or is already in use";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
