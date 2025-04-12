package com.chat.chat_online_be.service.external;

import com.chat.chat_online_be.security.JwtTokenProvider;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExternalServiceContainer {
    MessageSourceService messageSourceService;
    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtTokenProvider;
}
