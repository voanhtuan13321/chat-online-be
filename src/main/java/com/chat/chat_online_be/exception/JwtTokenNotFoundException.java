package com.chat.chat_online_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JwtTokenNotFoundException extends Exception {
    public JwtTokenNotFoundException(String message) {
        super(message);
    }
}
