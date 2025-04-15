package com.chat.chat_online_be.validation.password;

import com.chat.chat_online_be.model.request.RegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    /**
     * Initializes the validator with the PasswordMatches annotation.
     *
     * @param constraintAnnotation the PasswordMatches annotation
     */
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Performs the validation of the password fields.
     *
     * @param value the object being validated
     * @param context the validation context
     * @return true if the password fields match, false otherwise
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        RegisterRequest registerRequest = (RegisterRequest) value;
        return (registerRequest.getPassword() != null)
                && (registerRequest.getPassword().equals(registerRequest.getConfirmPassword()));
    }
}
