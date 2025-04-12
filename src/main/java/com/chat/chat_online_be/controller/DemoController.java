package com.chat.chat_online_be.controller;

import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.model.response.ApiResponse;
import com.chat.chat_online_be.model.response.ResponseEntityUtils;
import com.chat.chat_online_be.repository.RepositoryContainer;
import com.chat.chat_online_be.service.external.ExternalServiceContainer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/demo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class DemoController {
    ExternalServiceContainer externalServiceContainer;
    RepositoryContainer repositoryContainer;

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<UserEntity>> demo() {

        var user = getDemoUser(1L);
        return ResponseEntityUtils.success(ApiResponse.<UserEntity>builder()
                .data(user)
                .build());
    }

    public UserEntity getDemoUser(Long id) {
        var userOptional = externalServiceContainer.getRedisService().getHash("users", String.valueOf(id), UserEntity.class);

        return userOptional.orElseGet(() -> {
            log.info("demo get user with redis");
            var user = repositoryContainer.getUserRepository().findById(id).get();
            externalServiceContainer.getRedisService().setHash("users", String.valueOf(id), user);
            return user;
        });
    }
}
