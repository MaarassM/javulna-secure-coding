package com.kalavit.javulna.services;

import com.kalavit.javulna.model.RefreshToken;
import com.kalavit.javulna.model.User;
import com.kalavit.javulna.services.autodao.RefreshTokenAutoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenAutoDao refreshTokenDao;

    @Value("${app.jwt.refresh-token-ttl-days}")
    private long refreshTokenTtlDays;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(new Date(System.currentTimeMillis() + refreshTokenTtlDays * 24 * 60 * 60 * 1000));
        return refreshTokenDao.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenDao.findByToken(token);
    }

    public boolean isExpired(RefreshToken refreshToken) {
        return refreshToken.getExpiryDate().before(new Date());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenDao.deleteByUser(user);
    }
}