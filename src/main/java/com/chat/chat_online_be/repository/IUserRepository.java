package com.chat.chat_online_be.repository;

import com.chat.chat_online_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link UserEntity} instances.
 * <p>
 * Provides methods for retrieving and checking the existence of users
 * based on their username or email. This interface extends {@link JpaRepository},
 * which provides standard CRUD operations for entities.
 * </p>
 */
@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByEmail(String email);
  boolean existsByEmail(String email);
}