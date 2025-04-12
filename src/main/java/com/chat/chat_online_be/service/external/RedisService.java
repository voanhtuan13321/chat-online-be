package com.chat.chat_online_be.service.external;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service that provides basic operations for interacting with Redis,
 * including setting, retrieving, and deleting values using both regular keys and hash keys.
 *
 * <p>This class uses {@link RedisTemplate} with String keys and Object values.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisService {

    RedisTemplate<String, Object> redisTemplate;

    /**
     * Stores a value in Redis associated with the given key.
     *
     * @param key   the Redis key
     * @param value the value to store
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Retrieves a value from Redis by key and casts it to the specified type.
     *
     * @param key   the Redis key
     * @param clazz the expected class type
     * @param <T>   the generic type of the expected return value
     * @return an {@link Optional} containing the value if found and cast successfully; otherwise, an empty Optional
     */
    public <T> Optional<T> get(String key, Class<T> clazz) {
        return Optional
                .ofNullable(redisTemplate.opsForValue().get(key))
                .map(clazz::cast);
    }

    /**
     * Deletes a value from Redis by the given key.
     *
     * @param key the Redis key to delete
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Stores a value in a Redis hash under the given key and hashKey.
     *
     * @param key     the Redis hash key
     * @param hashKey the field within the hash
     * @param value   the value to store
     */
    public void setHash(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    /**
     * Retrieves a value from a Redis hash by key and hashKey, and casts it to the specified type.
     *
     * @param key     the Redis hash key
     * @param hashKey the field within the hash
     * @param clazz   the expected class type
     * @param <T>     the generic type of the expected return value
     * @return an {@link Optional} containing the value if found and cast successfully; otherwise, an empty Optional
     */
    public <T> Optional<T> getHash(String key, String hashKey, Class<T> clazz) {
        return Optional
                .ofNullable(redisTemplate.opsForHash().get(key, hashKey))
                .map(clazz::cast);
    }

    /**
     * Deletes a field from a Redis hash by the given key and hashKey.
     *
     * @param key     the Redis hash key
     * @param hashKey the field within the hash to delete
     */
    public void deleteHash(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }
}
