package com.chat.chat_online_be.controller;

import com.chat.chat_online_be.constant.ClassNamesConstant;
import com.chat.chat_online_be.exception.BadRequestException;
import com.chat.chat_online_be.exception.RefreshTokenException;
import com.chat.chat_online_be.model.response.ApiResponse;
import com.chat.chat_online_be.model.response.ResponseEntityUtils;
import com.chat.chat_online_be.service.external.MessageSourceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    // Inject dependencies
    MessageSourceService messageSourceService;

    /**
     * Handles authentication exceptions such as BadCredentialsException,
     * UsernameNotFoundException, and AuthenticationException.
     *
     * @param ex      the caught exception
     * @param request the HTTP request information
     * @return ResponseEntity<ApiResponse<?>> containing the authentication error response
     */
    @ExceptionHandler({ BadCredentialsException.class, UsernameNotFoundException.class, AuthenticationException.class })
    public ResponseEntity<ApiResponse<?>> handleAuthenticationExceptions(Exception ex, WebRequest request) {
        log.error("Authentication error: {}", ex.getMessage(), ex);

        String message = switch (ex.getClass().getSimpleName()) {
            case ClassNamesConstant.USERNAME_NOT_FOUND_EXCEPTION
                    -> messageSourceService.getMessage("Error.Unauthorized.UsernameNotFound");
            case ClassNamesConstant.AUTHENTICATION_EXCEPTION
                    -> messageSourceService.getMessage("Error.Unauthorized.UnauthorizedAccess");
            default -> messageSourceService.getMessage("Error.Unauthorized.Default");
        };

        return ResponseEntityUtils.unauthorized(ex, message);
    }

    /**
     * Handles AccessDeniedException by returning a 403 Forbidden response with an error message.
     *
     * @param ex      the caught exception
     * @param request the HTTP request information
     * @return ResponseEntity<ApiResponse<?>> containing the access denied error response
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntityUtils.forbidden(ex, messageSourceService.getMessage("Error.Forbidden.Default"));
    }

    /**
     * Handles bad request exceptions.
     *
     * @param ex      the caught exception
     * @param request the HTTP request information
     * @return ResponseEntity<ApiResponse<?>> containing the bad request error response
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex, WebRequest request) {
        return ResponseEntityUtils.badRequest(ex.getErrorMap(), messageSourceService.getMessage("Error.BadRequest.Default"));
    }

    /**
     * Handles refresh token exceptions.
     *
     * @param ex      the caught exception
     * @param request the HTTP request information
     * @return ResponseEntity<ApiResponse<?>> containing the forbidden error response
     */
    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleRefreshTokenException(RefreshTokenException ex, WebRequest request) {
        log.error("Refresh token error: {}", ex.getMessage(), ex);
        Map<String, String> errorMap = Map.of("refresh_token", ex.getMessage());
        return ResponseEntityUtils.forbidden(ex, "Lỗi khi làm mới token");
    }

    /**
     * Handles MethodArgumentNotValidException by returning a 400 Bad Request response with an error message.
     * The error message is a map of field names to error messages.
     *
     * @param ex      the caught exception
     * @param request the HTTP request information
     * @return ResponseEntity<ApiResponse<?>> containing the bad request error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errorMap = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("")));

        return ResponseEntityUtils.badRequest(errorMap, messageSourceService.getMessage("Error.BadRequest.Default"));
    }

    // @ExceptionHandler(HttpException.class)
    // public ResponseEntity<ApiErrorResponse> handleHttpException(HttpException ex,
    // WebRequest request) {
    // log.error(ex.getMessage(), ex);
    //
    // var errorMap = Map.of(
    // "error", ex.getMessage()
    // );
    // return ResponseApiEntity.badRequest(errorMap,
    // externalService.getMessageSource().getMessage("Error.BadRequest.Default"));
    // }

    /**
     * Handles all uncaught exceptions.
     *
     * @param ex      the caught exception
     * @param request the HTTP request information
     * @return ResponseEntity<ApiResponse<?>> containing the generic error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        return ResponseEntityUtils.internalServerError(ex, messageSourceService.getMessage("Error.InternalServerError.Default"));
    }
}
