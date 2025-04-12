package com.chat.chat_online_be.service.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalServiceContainer {
    IAuthenticationService authenticationService;
}
