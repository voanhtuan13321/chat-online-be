package com.chat.chat_online_be.model.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

/**
 * Utility class for creating ResponseEntity objects with various HTTP statuses
 * and ApiResponse bodies. This class provides methods for generating standard
 * HTTP responses with success, error, and status-specific configurations.
 * The methods in this class can be used to standardize the structure of HTTP
 * responses throughout the application, ensuring consistency in response
 * formatting and status codes.
 */
public class ResponseEntityUtils {
    /**
     * Returns a ResponseEntity with a status of OK (200) and an empty body.
     * Useful for operations that do not return any data, such as a DELETE operation.
     *
     * @return a ResponseEntity with an empty body
     */
    public static ResponseEntity<?> success() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Returns a ResponseEntity with an ApiResponse containing the given body
     * and a status of OK (200).
     *
     * @param body the ApiResponse to be returned in the ResponseEntity
     * @return a ResponseEntity with the given ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(ApiResponse<T> body) {
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    /**
     * Returns a ResponseEntity with an ApiResponse containing an appropriate
     * error message and a status of UNAUTHORIZED (401).
     *
     * @param ex      the caught exception
     * @param message the error message to be displayed in the ApiResponse
     * @return a ResponseEntity with an ApiResponse containing the error message
     */
    public static ResponseEntity<ApiResponse<?>> unauthorized(Exception ex, String message) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse
                        .builder()
                        .isSuccess(false)
                        .message(Optional.ofNullable(ex.getMessage()).orElse(message))
                        .error(ex.getMessage())
                        .build());
    }

    /**
     * Returns a ResponseEntity with an ApiResponse containing an appropriate
     * error message and a status of FORBIDDEN (403).
     *
     * @param ex      the caught exception
     * @param message the error message to be displayed in the ApiResponse
     * @return a ResponseEntity with an ApiResponse containing the error message
     */
    public static ResponseEntity<ApiResponse<?>> forbidden(Exception ex, String message) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse
                        .builder()
                        .isSuccess(false)
                        .message(Optional.ofNullable(ex.getMessage()).orElse(message))
                        .error(ex.getMessage())
                        .build());
    }

    /**
     * @return a ResponseEntity with a status of BAD_REQUEST (400)
     */
    public static ResponseEntity<?> badRequest() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Returns a ResponseEntity with an ApiResponse containing the given error map
     * and error message and a status of BAD_REQUEST (400).
     *
     * @param errorMap the map of error messages
     * @param message  the error message to be displayed in the ApiResponse
     * @return a ResponseEntity with a status of BAD_REQUEST and the ApiResponse
     * containing the given error map and error message
     */
    public static ResponseEntity<ApiResponse<?>> badRequest(Map<String, String> errorMap, String message) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse
                        .builder()
                        .isSuccess(false)
                        .message(message)
                        .error(errorMap)
                        .build());
    }

    /**
     * Returns a ResponseEntity with an ApiResponse containing an appropriate
     * error message and a status of INTERNAL_SERVER_ERROR (500).
     *
     * @param ex      the caught exception
     * @param message the error message to be displayed in the ApiResponse
     * @return a ResponseEntity with an ApiResponse containing the error message
     */
    public static ResponseEntity<ApiResponse<?>> internalServerError(Exception ex, String message) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse
                        .builder()
                        .isSuccess(false)
                        .message(Optional.ofNullable(ex.getMessage()).orElse(message))
                        .error(ex.getMessage())
                        .build());
    }
}
