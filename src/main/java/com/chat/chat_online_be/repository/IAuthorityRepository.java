package com.chat.chat_online_be.repository;

import com.chat.chat_online_be.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link AuthorityEntity} instances.
 * <p>
 * Provides methods for retrieving and checking the existence of authorities
 * based on their name. This interface extends {@link JpaRepository}, which
 * provides standard CRUD operations for entities.
 * </p>
 */
@Repository
public interface IAuthorityRepository extends JpaRepository<AuthorityEntity, Long> {
  boolean existsByName(String name);
}