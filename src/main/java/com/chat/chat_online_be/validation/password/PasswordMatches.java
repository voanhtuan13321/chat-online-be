package com.chat.chat_online_be.validation.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The PasswordMatches annotation is used to validate that two password fields match.
 * It is annotated with @Target(ElementType.TYPE) to indicate that it can be placed at the class level.
 * It is annotated with @Retention(RetentionPolicy.RUNTIME) to indicate that the annotation information will be available at runtime.
 * It is annotated with @Constraint(validatedBy = PasswordMatchesValidator.class) to specify the custom validator class to be used for validation.
 * The message() default value is set to "Passwords do not match" to provide a default error message.
 * The groups() default value is set to an empty array {} to indicate that the constraint applies to all validation groups.
 * The payload() default value is set to an empty array {} to indicate that no additional payload is required for the constraint.
 */
@Target(ElementType.TYPE) // Đặt annotation ở cấp lớp
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {
    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
