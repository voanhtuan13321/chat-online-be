package com.chat.chat_online_be.service.internal.impl;

import com.chat.chat_online_be.entity.AuthorityEntity;
import com.chat.chat_online_be.entity.RefreshTokenEntity;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.model.request.AuthenticationRequest;
import com.chat.chat_online_be.model.request.RegisterRequest;
import com.chat.chat_online_be.model.response.AuthenticationResponse;
import com.chat.chat_online_be.repository.IAuthorityRepository;
import com.chat.chat_online_be.repository.IUserRepository;
import com.chat.chat_online_be.security.JwtTokenProvider;
import com.chat.chat_online_be.service.internal.IAuthenticationService;
import com.chat.chat_online_be.service.internal.IRefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {

    // Inject another services
    AuthenticationManager authenticationManager;

    // Inject internal services
    IRefreshTokenService refreshTokenService;

    // Inject external services
    PasswordEncoder passwordEncoder;
    JwtTokenProvider jwtTokenProvider;

    // Inject repositories
    IAuthorityRepository authorityRepository;
    IUserRepository userRepository;

    /**
     * Registers a new user with IP and device information.
     *
     * @param registerRequest The request containing the user's registration
     *                        details.
     * @param ipAddress       The IP address of the user.
     * @param deviceInfo      The device information of the user.
     * @return An authentication response containing tokens.
     */
    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest, String ipAddress, String deviceInfo) {
        AuthorityEntity userAuthor = authorityRepository.findById(2L).orElseThrow();
        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .authorities(List.of(userAuthor))
                .build();
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user, ipAddress, deviceInfo);

        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * Authenticates an existing user with IP and device information.
     *
     * @param authenticationRequest The request containing the user's authentication
     *                              details.
     * @param ipAddress             The IP address of the user.
     * @param deviceInfo            The device information of the user.
     * @return An authentication response containing tokens.
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest,
                                               String ipAddress,
                                               String deviceInfo) {
        // Create an Authentication object using the provided email and password
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword());

        // Authenticate the user credentials using the authentication manager
        authenticationManager.authenticate(authentication);

        // Retrieve the user entity from the database by email
        UserEntity user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();

        // Generate a JWT access token for the authenticated user
        String token = jwtTokenProvider.generateToken(user);

        // Create a new refresh token for the user with IP and device info
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user, ipAddress, deviceInfo);

        // Build and return the authentication response containing both tokens
        return AuthenticationResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /**
     * Logs out the user by invalidating the refresh token.
     *
     * @param refreshToken The refresh token to invalidate.
     * @return true if logout was successful; false otherwise.
     */
    @Override
    @Transactional
    public boolean logout(String refreshToken) {
        return refreshTokenService.deleteRefreshToken(refreshToken);
    }
}
