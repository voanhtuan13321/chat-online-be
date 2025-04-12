package com.chat.chat_online_be.controller;

import com.chat.chat_online_be.exception.BadRequestException;
import com.chat.chat_online_be.model.request.AuthenticationRequest;
import com.chat.chat_online_be.model.request.RegisterRequest;
import com.chat.chat_online_be.model.response.ApiResponse;
import com.chat.chat_online_be.model.response.AuthenticationResponse;
import com.chat.chat_online_be.model.response.ResponseEntityUtils;
import com.chat.chat_online_be.service.external.ExternalServiceContainer;
import com.chat.chat_online_be.service.internal.InternalServiceContainer;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles authentication and registration requests.
 * <p>
 * The controller provides endpoints for registering a new user and authenticating an existing user.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class AuthenticationController {
    ExternalServiceContainer externalServiceContainer;
    InternalServiceContainer internalServiceContainer;

    /**
     * Registers a new user.
     * endpoint: "/api/v1/auth/register".
     *
     * @param registerRequest The request containing the user's registration details.
     * @return A response entity with an API response containing the registration result.
     * @throws BadRequestException If the request is invalid.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var authenticationResponse = internalServiceContainer.getAuthenticationService().register(registerRequest);

        return ResponseEntityUtils.success(
                ApiResponse.<AuthenticationResponse>builder()
                        .data(authenticationResponse)
                        .build()
        );
    }

    /**
     * Authenticates a user.
     * endpoint: "/api/v1/auth/authenticate".
     *
     * @param authenticationRequest The request containing the user's authentication details.
     * @return A response entity with an API response containing the authentication result.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        var authenticationResponse = internalServiceContainer.getAuthenticationService().authenticate(authenticationRequest);
        return ResponseEntityUtils.success(
                ApiResponse.<AuthenticationResponse>builder()
                        .data(authenticationResponse)
                        .build()
        );
    }
}
