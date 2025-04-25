package com.inspire17.ythelper.validation;

import com.inspire17.ythelper.validation.annotations.FullName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class FullNameVerification implements ConstraintValidator<FullName, String> {
    private static final String FULL_NAME_REGEX = "^[A-Za-z]+([ '-][A-Za-z]+)+$";

    @Override
    public boolean isValid(String fullName, ConstraintValidatorContext constraintValidatorContext) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        return fullName.matches(FULL_NAME_REGEX);
    }
}
