package com.kalavit.javulna.services.autodao;

import com.kalavit.javulna.model.RefreshToken;
import com.kalavit.javulna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenAutoDao extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}