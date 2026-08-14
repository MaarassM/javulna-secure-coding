package com.kalavit.javulna.controllers.rest;

import com.kalavit.javulna.dto.AuthRequestDto;
import com.kalavit.javulna.dto.JwtResponseDto;
import com.kalavit.javulna.dto.RefreshRequestDto;
import com.kalavit.javulna.dto.UserDto;
import com.kalavit.javulna.model.RefreshToken;
import com.kalavit.javulna.model.User;
import com.kalavit.javulna.security.JwtService;
import com.kalavit.javulna.services.RefreshTokenService;
import com.kalavit.javulna.services.autodao.UserAutoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/rest/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserAutoDao userDao;

    @Value("${app.jwt.access-token-ttl-minutes}")
    private long accessTokenTtlMinutes;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody AuthRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userDao.findUserByName(request.getUsername());
        String accessToken = jwtService.generateAccessToken(user.getName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        JwtResponseDto response  = new JwtResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setExpiresInSeconds(accessTokenTtlMinutes * 60);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequestDto request) {

        Optional<RefreshToken> maybeToken = refreshTokenService.findByToken(request.getRefreshToken());
        if  (!maybeToken.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        RefreshToken refreshToken = maybeToken.get();

        if (refreshTokenService.isExpired(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User userToken = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(userToken.getName());
        JwtResponseDto response  = new JwtResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken.getToken());
        response.setExpiresInSeconds(accessTokenTtlMinutes * 60);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequestDto request) {

        Optional<RefreshToken> maybeToken = refreshTokenService.findByToken(request.getRefreshToken());

        if (!maybeToken.isPresent()) {
            return ResponseEntity.ok().build();
        }

        RefreshToken refreshToken = maybeToken.get();
        refreshTokenService.deleteByUser(refreshToken.getUser());

        return ResponseEntity.ok().build();

    }
}