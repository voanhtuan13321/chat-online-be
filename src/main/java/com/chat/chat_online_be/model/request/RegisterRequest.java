package com.chat.chat_online_be.model.request;

import com.chat.chat_online_be.validation.password.PasswordMatches;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Represents a request containing user registration information.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches(message = "Confirm password do not match the password")
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    @Email(message = "Email not valid")
    private String email;
}
