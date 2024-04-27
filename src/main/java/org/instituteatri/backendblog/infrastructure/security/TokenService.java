package org.instituteatri.backendblog.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${security.token.private-key}")
    private RSAPrivateKey privateKey;

    @Value("${security.token.public-key}")
    private RSAPublicKey publicKey;

    @Value("${security.token.expiration-token}")
    private Integer expirationToken;

    @Value("${security.token.expiration-refresh-token}")
    private Integer refreshTokenExpiration;

    private final TokenRepository tokenRepository;

    private final IPBlockingService ipBlockingService;

    private final HttpServletRequest request;


    public String generateToken(User user, Integer expiration) {
        try {
            Algorithm algorithm = getAlgorithm();
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("Name", user.getName())
                    .withAudience(user.getRole().name())
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(getExpirationDate(expiration))
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            log.error("[TOKEN_INVALID] Error while generating token for user {}: {}", user.getEmail(), exception.getMessage());
            throw new TokenGenerationException("Error while generating token", exception);
        }
    }

    public String generateAccessToken(User user) {
        String token = generateToken(user, expirationToken);
        log.info("[TOKEN_SUCCESS] Generated access token for user {}: {}", user.getEmail(), token);
        return token;
    }

    public String generateRefreshToken(User user) {
        String refreshToken = generateToken(user, refreshTokenExpiration);
        log.info("[TOKEN_SUCCESS] Generated refresh token for user {}: {}", user.getEmail(), refreshToken);
        return refreshToken;
    }

    public String validateToken(String token) {
        String ipAddress = ipBlockingService.getRealClientIP();
        String userAgent = request.getHeader("User-Agent");

        if (ipBlockingService.isBlocked(ipAddress)) {
            log.warn("[BLOCKED_IP] Access denied for blocked IP address and User-Agent: {} - {}", ipAddress, userAgent);
            return null;
        }

        return verifyToken(token, ipAddress, userAgent);
    }

    private String verifyToken(String token, String ipAddress, String userAgent) {

        try {
            Algorithm algorithm = getAlgorithm();
            DecodedJWT decodedJWT = JWT
                    .require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token);

            Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);
            if (optionalToken.isEmpty()) {
                log.warn("[TOKEN_NOT_FOUND] Token not found in the database: {}", token);
                return null;
            }

            Token dbToken = optionalToken.get();
            if (dbToken.isRevoked()) {
                log.warn("[TOKEN_REVOKED] Token is revoked: {}", token);
                return null;
            }

            String subject = decodedJWT.getSubject();
            log.info("[TOKEN_SUCCESS] Token is valid for subject: {}", subject);

            return subject;

        } catch (JWTVerificationException exception) {
            log.error("[TOKEN_INVALID] JWT verification failed, exception: {}", exception.getMessage());
            ipBlockingService.registerFailedAttempt(ipAddress, userAgent);
            return null;
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.RSA256(publicKey, privateKey);
    }

    private Instant getExpirationDate(Integer expiration) {
        return LocalDateTime.now().plusMinutes(expiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
