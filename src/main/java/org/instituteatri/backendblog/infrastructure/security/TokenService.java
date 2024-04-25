package org.instituteatri.backendblog.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private Integer expiration;

    @Value("${api.security.token.refresh-token-expiration}")
    private Integer refreshTokenExpiration;

    /**
     * Generates a JSON Web Token (JWT) for the given user.
     *
     * @param user the user for whom the JWT is to be generated
     * @return the JWT for the given user
     * @throws TokenGenerationException if an error occurs while generating the JWT
     */
    public String generateToken(User user, String audience, Integer expiration) {
        try {
            Algorithm algorithm = getAlgorithm(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("Name", user.getName())
                    .withClaim("Role", user.getRole().name())
                    .withAudience(audience)
                    .withExpiresAt(getExpirationDate(expiration))
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            log.error("[TOKEN] Error while generating token for user {}: {}", user.getEmail(), exception.getMessage());
            throw new TokenGenerationException("Error while generating token", exception);
        }
    }

    public String generateAccessToken(User user) {
        String token = generateToken(user, "backend-api", expiration);
        log.info("[TOKEN] Generated access token for user {}: {}", user.getEmail(), token);
        return token;
    }

    public String generateRefreshToken(User user) {
        String refreshToken = generateToken(user, "refresh-token-api", refreshTokenExpiration);
        log.info("[TOKEN] Generated refresh token for user {}: {}", user.getEmail(), refreshToken);
        return refreshToken;
    }


    /**
     * Checks if the given JWT token is valid and not blocklisted.
     * If the token is valid, the function returns the subject of the token.
     * If the token is invalid or blocklisted, the function throws an exception.
     *
     * @param token the JWT token to be verified
     * @return the subject of the verified token
     * @throws TokenInvalidException if the token is invalid or blocklisted
     */
    public String isValidateToken(String token) {
        try {
            Algorithm algorithm = getAlgorithm(secret);
            if (JWT.require(algorithm).withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getExpiresAt()
                    .before(new Date())) {

                log.warn("[TOKEN] Token is expired: {}", token);
                return null;
            }

            String subject = JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();

            log.info("[TOKEN] Token is valid for subject: {}", subject);
            return subject;
        } catch (JWTVerificationException exception) {
            log.error("[TOKEN] Token is invalid: {}", token);
            return null;
        }
    }

    private Algorithm getAlgorithm(String secretKey) {
        return Algorithm.HMAC256(secretKey);
    }

    private Instant getExpirationDate(Integer expiration) {
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));
    }
}
