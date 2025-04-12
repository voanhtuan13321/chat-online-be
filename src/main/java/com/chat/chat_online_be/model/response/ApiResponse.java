package com.chat.chat_online_be.model.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Represents a generic API response.
 *
 * @param <T> the type of the response body
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApiResponse<T> {
    @Builder.Default
    boolean isSuccess = true;
    String message;
    T data;
    Object error;
}
