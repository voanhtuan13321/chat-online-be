package com.chat.chat_online_be.model.request;

import com.chat.chat_online_be.validation.password.PasswordMatches;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a request containing user registration information.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@PasswordMatches(message = "Confirm password do not match the password")
public class RegisterRequest {
    @NotBlank
    String username;
    @NotBlank
    String password;
    @NotBlank
    String confirmPassword;
    @NotBlank
    @Email(message = "Email not valid")
    String email;
}
