package com.chat.chat_online_be.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a request containing authentication credentials.
 * This includes the username and password fields.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    String email;
    String password;
}
