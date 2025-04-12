package com.chat.chat_online_be.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Represents an exception for bad requests, capturing an error map
 * and a detailed message. This exception is annotated to produce
 * a 400 Bad Request HTTP status code when thrown in a web context.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BadRequestException extends RuntimeException {
    Map<String, String> errorMap;

    public BadRequestException(Map<String, String> errorMap, String message) {
        super(message);
        this.errorMap = errorMap;
    }
}
