package com.chat.chat_online_be.service.internal;

import com.chat.chat_online_be.model.request.AuthenticationRequest;
import com.chat.chat_online_be.model.request.RegisterRequest;
import com.chat.chat_online_be.model.response.AuthenticationResponse;

public interface IAuthenticationService {
    AuthenticationResponse register(RegisterRequest registerRequest, String ipAddress, String deviceInfo);
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, String ipAddress, String deviceInfo);
    boolean logout(String refreshToken);
}
