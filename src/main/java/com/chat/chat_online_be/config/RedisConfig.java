package com.chat.chat_online_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Configuration class for Redis setup.
 * <p>
 * Defines a {@link RedisTemplate} bean for interacting with Redis using string keys and generic object values.
 */
@Configuration
@SuppressWarnings("unused")
public class RedisConfig {

    /**
     * Creates a {@link RedisTemplate} bean to interact with Redis.
     *
     * @param connectionFactory the connection factory used to connect to the Redis server
     * @return a RedisTemplate with String keys and Object values
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
