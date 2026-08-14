package com.kalavit.javulna.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    public static final String TYPE_CLAIM = "typ_claim";
    public static final String ACCESS_TOKEN_TYPE = "access";

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-ttl-minutes}")
    private long accessTokenTtlMinutes;

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenTtlMinutes * 60 *1000);

        return Jwts.builder()
                .issuer(issuer)
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claim(TYPE_CLAIM, ACCESS_TOKEN_TYPE)
                .signWith(getSignKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = extractAllClaims(token);
            return username.equals(claims.getSubject())
                    && claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }
}
