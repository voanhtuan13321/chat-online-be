package com.chat.chat_online_be.service.internal.impl;

import com.chat.chat_online_be.entity.RefreshTokenEntity;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.exception.RefreshTokenException;
import com.chat.chat_online_be.repository.IRefreshTokenRepository;
import com.chat.chat_online_be.repository.IUserRepository;
import com.chat.chat_online_be.service.internal.IRefreshTokenService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService implements IRefreshTokenService {

    // Inject repository
    IUserRepository userRepository;
    IRefreshTokenRepository refreshTokenRepository;

    Duration EXPIRATION_TIME = Duration.ofDays(1);

    /**
     * Creates a new refresh token for the user with the given email, IP address, and device information.
     * If the user already has an existing refresh token, it will be deleted and replaced with a new one.
     *
     * @param user       The user for whom to create the refresh token.
     * @param ipAddress  The IP address from which the request originated.
     * @param deviceInfo Information about the user's device.
     * @return The newly created and saved RefreshTokenEntity.
     * @throws RuntimeException if the user is not found by the provided email.
     */
    @Override
    public RefreshTokenEntity createRefreshToken(UserEntity user, String ipAddress, String deviceInfo) {
        // Check if the user already has a refresh token and delete it if present
        Optional<RefreshTokenEntity> existingToken = refreshTokenRepository.findByUser(user);
        existingToken.ifPresent(refreshTokenRepository::delete);

        // Build a new refresh token entity with 1 day expiry, random token, and device info
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .expiryDate(Instant.now().plus(EXPIRATION_TIME))
                .token(UUID.randomUUID().toString())
                .ipAddress(ipAddress)
                .deviceInfo(deviceInfo)
                .build();

        // Save and return the new refresh token
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verifies whether the provided refresh token has expired.
     * If the token is expired, it will be deleted from the repository and a RefreshTokenException is thrown.
     *
     * @param token The refresh token entity to verify.
     * @throws RefreshTokenException If the token has expired.
     */
    @Override
    public void verifyExpiration(RefreshTokenEntity token) throws RefreshTokenException {
        // Check if the token's expiry date is before the current time
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            // Delete the expired token from the repository
            refreshTokenRepository.delete(token);
            // Throw exception indicating the token has expired
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Please make a new signing request");
        }
    }

    /**
     * Rotates the given refresh token with a new one for the same user.
     * Checks for suspicious activity by verifying IP address and device information.
     * If any mismatch is detected, a RefreshTokenException is thrown.
     * The old token is deleted and a new token is created and saved.
     *
     * @param currentToken The existing refresh token to be rotated.
     * @param ipAddress    The current IP address of the user (can be null).
     * @param deviceInfo   The current device information of the user (can be null).
     * @return The newly created RefreshTokenEntity.
     * @throws RefreshTokenException If suspicious activity is detected (IP or device mismatch).
     */
    public RefreshTokenEntity rotateRefreshToken(RefreshTokenEntity currentToken, String ipAddress, String deviceInfo) {
        // Check for IP address mismatch if both are present
        if (currentToken.getIpAddress() != null &&
                ipAddress != null &&
                !currentToken.getIpAddress().equals(ipAddress)) {
            throw new RefreshTokenException(currentToken.getToken(), "Suspicious activity detected: IP address mismatch. Please login again.");
        }

        // Check for device information mismatch if both are present
        if (currentToken.getDeviceInfo() != null &&
                deviceInfo != null &&
                !currentToken.getDeviceInfo().equals(deviceInfo)) {
            throw new RefreshTokenException(currentToken.getToken(), "Suspicious activity detected: Device information mismatch. Please login again.");
        }

        // Delete the old refresh token
        refreshTokenRepository.delete(currentToken);

        // Create a new refresh token for the same user
        RefreshTokenEntity newToken = RefreshTokenEntity.builder()
                .user(currentToken.getUser())
                .expiryDate(Instant.now().plus(EXPIRATION_TIME))
                .token(UUID.randomUUID().toString())
                .ipAddress(ipAddress != null ? ipAddress : currentToken.getIpAddress())
                .deviceInfo(deviceInfo != null ? deviceInfo : currentToken.getDeviceInfo())
                .build();

        // Save and return the new refresh token
        return refreshTokenRepository.save(newToken);
    }

    /**
     * Finds a refresh token entity by its token string.
     *
     * @param token The refresh token string to search for.
     * @return An Optional containing the RefreshTokenEntity if found, or empty if not found.
     */
    @Override
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Deletes the refresh token with the given token string if it exists.
     * Returns true if the token was found and deleted, false otherwise.
     * This method is transactional to ensure consistency.
     *
     * @param token The refresh token string to revoke.
     * @return true if the token was found and deleted, false otherwise.
     */
    @Override
    @Transactional
    public boolean deleteRefreshToken(String token) {
        // Attempt to find the refresh token entity by its token string
        Optional<RefreshTokenEntity> refreshTokenOpt = refreshTokenRepository.findByToken(token);

        if (refreshTokenOpt.isPresent()) {
            // If the token exists, delete it from the repository
            RefreshTokenEntity refreshToken = refreshTokenOpt.get();
            refreshTokenRepository.delete(refreshToken);
            return true; // Indicate that the token was found and deleted
        }

        return false; // Indicate that no token was found to delete
    }
}