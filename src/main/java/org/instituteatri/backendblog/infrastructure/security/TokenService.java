package org.instituteatri.backendblog.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.token.InvalidToken;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenInvalidException;
import org.instituteatri.backendblog.repository.InvalidTokenRepository;
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
    private final InvalidTokenRepository invalidTokenRepository;

    /**
     * Generates a JSON Web Token (JWT) for the given user.
     *
     * @param user the user for whom the JWT is to be generated
     * @return the JWT for the given user
     * @throws TokenGenerationException if an error occurs while generating the JWT
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("Name", user.getName())
                    .withClaim("Role", user.getRole().name())
                    .withAudience("backend-api")
                    .withExpiresAt(getExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException("Error while generating token", exception);
        }
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

        if (invalidTokenRepository.existsByToken(token)) {
            log.warn("Token is invalid due to being blocklisted: {}", token);
            throw new TokenInvalidException(token);
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            if (JWT.require(algorithm).withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getExpiresAt()
                    .before(new Date())) {

                log.warn("Token is expired: {}", token);
                throw new TokenInvalidException(token);
            }

            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            log.warn("Token is invalid: {}", token);
            throw new TokenInvalidException(token);
        }
    }


    public void invalidateToken(String token) {
        if (!invalidTokenRepository.existsByToken(token)) {
            invalidTokenRepository.save(new InvalidToken(token));
        }
    }

    public boolean isTokenInvalid(String token) {
        return invalidTokenRepository.existsByToken(token);
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }
}
