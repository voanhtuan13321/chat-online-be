package com.chat.chat_online_be.security;

import com.chat.chat_online_be.constant.ClassNameConstants;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.exception.JwtTokenNotFoundException;
import com.chat.chat_online_be.model.response.ApiResponse;
import com.chat.chat_online_be.service.external.ExternalServiceContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A filter that authenticates incoming requests using a JWT token.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    UserDetailsService userDetailsService;
    ExternalServiceContainer externalService;

    /**
     * Examines the request for the presence of a JWT token and validates it
     * using the configured {@link JwtTokenProvider}. If the token is valid, the
     * user associated with the token is loaded from the configured
     * {@link UserDetailsService} and the user is set in the
     * {@link SecurityContextHolder}.
     *
     * @param request     the request to examine
     * @param response    the response to which the filter chain is writing
     * @param filterChain the filter chain to which the request and response are
     *                    passed
     * @throws ServletException if an error occurs while processing the request
     * @throws IOException      if an error occurs while writing the response
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // Get the JWT token from the request
            var jwt = getJwtFromRequest(request);

            // Extract the email from the JWT token
            var email = externalService.getJwtTokenProvider().getSubjectFromJWT(jwt);

            // Check if the user is already authenticated
            if (StringUtils.hasText(email) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
                // Load the user details from the user service
                var userDetails = userDetailsService.loadUserByUsername(email);

                if (externalService.getJwtTokenProvider().isTokenValid(jwt, (UserEntity) userDetails)) {
                    // Create an authentication object based on the user details,
                    // and add request details to the authentication object
                    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication object in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (JwtTokenNotFoundException ex) {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            // Log the error and send an error response
            logger.error(ex.getMessage(), ex);

            // set status
            response.setStatus(getErrorStatus(ex));

            // send error response
            sendErrorResponse(response, ApiResponse
                    .builder()
                    .isSuccess(false)
                    .message(getErrorMessage(ex))
                    .error(ex.getMessage())
                    .build());
        }
    }

    /**
     * Extracts the JWT token from the Authorization header of the request.
     *
     * @param request the HTTP request
     * @return the JWT token, or null if not found
     */
    private String getJwtFromRequest(HttpServletRequest request) throws JwtTokenNotFoundException {
        // Get the Authorization header from the request
        var bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        var BEARER_PREFIX = "Bearer ";

        // Check if the header is present and starts with the Bearer prefix
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            // Extract the JWT token by removing the Bearer prefix
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        // throw exception when JWT token is missing
        throw new JwtTokenNotFoundException("JWT token is missing or invalid");
    }


    /**
     * This filter is responsible for authenticating the user based on the
     * JWT token sent in the Authorization header of the request.
     * The filter chain is only continued if the authentication is successful.
     * In case of an error, an error response is sent with the appropriate status
     * code and error message.
     */
    private String getErrorMessage(Exception ex) {
        return switch (ex.getClass().getSimpleName()) {
            case ClassNameConstants.ExpiredJwtException
                    -> externalService.getMessageSourceService().getMessage("Error.Unauthorized.TokenHasExpired");
            case ClassNameConstants.UnsupportedJwtException
                    -> externalService.getMessageSourceService().getMessage("Error.Unauthorized.UnsupportedJWTToken");
            case ClassNameConstants.MalformedJwtException
                    -> externalService.getMessageSourceService().getMessage("Error.Unauthorized.MalformedJWTToken");
            case ClassNameConstants.IllegalArgumentException
                    -> externalService.getMessageSourceService().getMessage("Error.Unauthorized.JWTTokenIsMissingOrInvalid");
            default
                    -> externalService.getMessageSourceService().getMessage("Error.InternalServerError.Default"); // For other unknown exceptions
        };
    }


    /**
     * Given an exception, returns the corresponding HTTP status code to be used
     * in the response.
     * <p>
     * The following exceptions are handled:
     * <ul>
     *     <li>{@link ExpiredJwtException}: {@link HttpServletResponse#SC_UNAUTHORIZED}</li>
     *     <li>Other exceptions: {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR}</li>
     * </ul>
     * If the exception is not any of the above, the default status code is
     * {@link HttpServletResponse#SC_INTERNAL_SERVER_ERROR}.
     *
     * @param ex the exception from which to derive the status code
     * @return the HTTP status code to be used in the response
     */
    private int getErrorStatus(Exception ex) {
        return (ex instanceof ExpiredJwtException)
                ? HttpServletResponse.SC_UNAUTHORIZED
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 400 for other errors
    }


    /**
     * Writes the given {@link ApiResponse} to the given
     * {@link HttpServletResponse} as a JSON response.
     *
     * @param response the {@link HttpServletResponse} to write the error response to
     * @param apiResponse the {@link ApiResponse} to write
     * @throws IOException if an error occurs while writing the response
     */
    private void sendErrorResponse(HttpServletResponse response, ApiResponse<?> apiResponse) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        response.getWriter().flush();
    }
}