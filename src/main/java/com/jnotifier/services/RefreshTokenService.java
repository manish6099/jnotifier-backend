package com.jnotifier.services;

import java.util.Optional;
import com.jnotifier.entity.RefreshToken;

public interface RefreshTokenService {
  Optional<RefreshToken> findByToken(String token);
  RefreshToken createRefreshToken(Long userId);
  RefreshToken verifyExpiration(RefreshToken token);
  int deleteByUserId(Long userId);
}
