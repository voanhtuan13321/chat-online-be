package com.chat.chat_online_be.service.internal.impl;

import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.model.request.AuthenticationRequest;
import com.chat.chat_online_be.model.request.RegisterRequest;
import com.chat.chat_online_be.model.response.AuthenticationResponse;
import com.chat.chat_online_be.repository.RepositoryContainer;
import com.chat.chat_online_be.service.external.ExternalServiceContainer;
import com.chat.chat_online_be.service.internal.IAuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {

    ExternalServiceContainer externalService;
    AuthenticationManager authenticationManager;
    RepositoryContainer repositoryContainer;

    /**
     * Registers a new user based on the provided registration request.
     * It creates a new user from the request details, saves the user to the database, and returns a response containing the user's JWT token.
     *
     * @param registerRequest The request containing the user's registration details.
     * @return An ApiResponse containing the authentication response.
     */
    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var userAuthor = repositoryContainer.getAuthorityRepository().findById(2L).orElseThrow();
        var user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(externalService.getPasswordEncoder().encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .authorities(List.of(userAuthor))
                .build();
        repositoryContainer.getUserRepository().save(user);

        var token = externalService.getJwtTokenProvider().generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(token)
                .build();
    }

    /**
     * Authenticates an existing user based on the provided authentication request.
     * It authenticates the user, retrieves the user's details from the database, generates a JWT token,
     * and returns a response containing the user's JWT token.
     *
     * @param authenticationRequest The request containing the user's authentication details.
     * @return An ApiResponse containing the authentication response.
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        );
        authenticationManager.authenticate(authentication);

        var user = repositoryContainer
                .getUserRepository()
                .findByEmail(authenticationRequest.getEmail())
                .orElseThrow();
        var token = externalService.getJwtTokenProvider().generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(token)
                .build();
    }
}
