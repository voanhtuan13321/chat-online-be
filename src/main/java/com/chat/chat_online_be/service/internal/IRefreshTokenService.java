package com.chat.chat_online_be.service.internal;

import com.chat.chat_online_be.entity.RefreshTokenEntity;
import com.chat.chat_online_be.entity.UserEntity;
import com.chat.chat_online_be.exception.RefreshTokenException;

import java.util.Optional;

public interface IRefreshTokenService {
  RefreshTokenEntity createRefreshToken(UserEntity user, String ipAddress, String deviceInfo);

  void verifyExpiration(RefreshTokenEntity token) throws RefreshTokenException;

  RefreshTokenEntity rotateRefreshToken(RefreshTokenEntity currentToken, String ipAddress, String deviceInfo);

  Optional<RefreshTokenEntity> findByToken(String token);

  boolean deleteRefreshToken(String token);
}