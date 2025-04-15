package com.chat.chat_online_be.repository;

import com.chat.chat_online_be.entity.RefreshTokenEntity;
import com.chat.chat_online_be.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  Optional<RefreshTokenEntity> findByToken(String token);

  Optional<RefreshTokenEntity> findByUser(UserEntity user);

  @Modifying
  int deleteByUser(UserEntity user);
}