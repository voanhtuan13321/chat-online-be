package com.chat.chat_online_be.controller;

import com.chat.chat_online_be.constant.HttpHeadersConstant;
import com.chat.chat_online_be.entity.RefreshTokenEntity;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.exception.BadRequestException;
import com.chat.chat_online_be.exception.RefreshTokenException;
import com.chat.chat_online_be.model.request.AuthenticationRequest;
import com.chat.chat_online_be.model.request.LogoutRequest;
import com.chat.chat_online_be.model.request.RefreshTokenRequest;
import com.chat.chat_online_be.model.request.RegisterRequest;
import com.chat.chat_online_be.model.response.ApiResponse;
import com.chat.chat_online_be.model.response.AuthenticationResponse;
import com.chat.chat_online_be.model.response.ResponseEntityUtils;
import com.chat.chat_online_be.security.JwtTokenProvider;
import com.chat.chat_online_be.service.internal.IAuthenticationService;
import com.chat.chat_online_be.service.internal.IRefreshTokenService;
import com.chat.chat_online_be.util.DeviceInfoUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    // Inject internal service
    IAuthenticationService authenticateService;
    IRefreshTokenService refreshTokenService;

    // Inject external service
    JwtTokenProvider jwtTokenProvider;

    /**
     * Registers a new user.
     * endpoint: "/api/v1/auth/register".
     *
     * @param registerRequest The request containing the user's registration details.
     * @return A response entity with an API response containing the registration result.
     * @throws BadRequestException If the request is invalid.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest registerRequest,
                                                                        HttpServletRequest request) {
        // get ipaddress and device info
        String ipAddress = getClientIpAddress(request);
        String deviceInfo = DeviceInfoUtil.extractDeviceInfo(request);

        // register
        AuthenticationResponse authenticationResponse = authenticateService.register(registerRequest, ipAddress, deviceInfo);

        // TODO: send email

        return ResponseEntityUtils.success(
                ApiResponse.<AuthenticationResponse>builder()
                        .data(authenticationResponse)
                        .build());
    }

    /**
     * Authenticates a user.
     * endpoint: "/api/v1/auth/authenticate".
     *
     * @param authenticationRequest The request containing the user's authentication details.
     * @return A response entity with an API response containing the authentication
     * result.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest authenticationRequest,
                                                                            HttpServletRequest request) {
        // get ipaddress and device info
        String ipAddress = getClientIpAddress(request);
        String deviceInfo = DeviceInfoUtil.extractDeviceInfo(request);

        // authenticate
        AuthenticationResponse authenticationResponse = authenticateService.authenticate(authenticationRequest, ipAddress, deviceInfo);

        return ResponseEntityUtils.success(
                ApiResponse.<AuthenticationResponse>builder()
                        .data(authenticationResponse)
                        .build());
    }

    /**
     * Refreshes an existing token using a refresh token.
     * endpoint: "/api/v1/auth/refresh-token".
     *
     * @param request The request containing the refresh token.
     * @return A response entity with an API response containing the new tokens.
     * @throws RefreshTokenException If the refresh token is invalid or expired.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
                                                                            HttpServletRequest request) {
        // get refresh token, ipaddress and device info
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        String ipAddress = getClientIpAddress(request);
        String deviceInfo = refreshTokenRequest.getDeviceInfo() != null
                ? refreshTokenRequest.getDeviceInfo()
                : DeviceInfoUtil.extractDeviceInfo(request);

        // find refresh token by token
        RefreshTokenEntity refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));

        // verify expiration
        refreshTokenService.verifyExpiration(refreshToken);

        // refresh token rotation (creating new and deleting old)
        RefreshTokenEntity newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, ipAddress, deviceInfo);

        // get user from refresh token
        UserEntity user = newRefreshToken.getUser();

        // generate new access token
        String accessToken = jwtTokenProvider.generateToken(user);

        return ResponseEntityUtils.success(
                ApiResponse.<AuthenticationResponse>builder()
                        .data(AuthenticationResponse.builder()
                                .token(accessToken)
                                .refreshToken(newRefreshToken.getToken())
                                .build())
                        .message("Refresh token renewed")
                        .build());
    }

    /**
     * Log out user by invalidating refresh token.
     * endpoint: "/api/v1/auth/logout".
     *
     * @param logoutRequest Request containing the refresh token to be invalidated
     * @return Response entity with logout result
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        boolean isLogoutSuccess = authenticateService.logout(logoutRequest.getRefreshToken());

        if (isLogoutSuccess) {
            return ResponseEntityUtils.success(
                    ApiResponse.<Boolean>builder()
                            .data(true)
                            .message("Đăng xuất thành công")
                            .build());
        } else {
            return ResponseEntityUtils.success(
                    ApiResponse.<Boolean>builder()
                            .data(false)
                            .message("Refresh token không tồn tại hoặc đã hết hạn")
                            .build());
        }
    }

    /**
     * Extracts the client's IP address from the HTTP request.
     * This method checks common headers used when requests pass through proxies,
     * and falls back to the remote address if no proxy headers are found.
     *
     * @param request The HTTP servlet request
     * @return The client's IP address as a string
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // List of headers to check in order of preference
        String[] headers = {
                HttpHeadersConstant.X_FORWARDED_FOR,
                HttpHeadersConstant.PROXY_CLIENT_IP,
                HttpHeadersConstant.WL_PROXY_CLIENT_IP,
                HttpHeadersConstant.HTTP_CLIENT_IP,
                HttpHeadersConstant.HTTP_X_FORWARDED_FOR
        };

        String ipAddress = null;

        // Check each header until a valid IP is found
        for (String header : headers) {
            ipAddress = request.getHeader(header);

            // Break the loop if a valid IP is found
            if (isValidIp(ipAddress)) {
                break;
            }
        }

        // Fall back to remote address if no valid IP found in headers
        if (!isValidIp(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // If multiple IPs are in X-Forwarded-For, extract the first one (client IP)
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    /**
     * Checks if the IP address string is valid (not null, empty, or "unknown").
     *
     * @param ip The IP address string to validate
     * @return true if the IP is valid, false otherwise
     */
    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }
}
