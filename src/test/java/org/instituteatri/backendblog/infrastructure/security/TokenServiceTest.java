package org.instituteatri.backendblog.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.entities.UserRole;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private IPBlockingService ipBlockingService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TokenService tokenService;

    private final User user = new User();
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private final String ipAddress = "127.0.0.1";
    private final String userAgent = "Mozilla/5.0";
    private final String userEmail = "test@example.com";
    private String validToken;

    @BeforeEach
    void setup() throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();

        tokenService.setPrivateKey(privateKey);
        tokenService.setPublicKey(publicKey);

        user.setEmail(userEmail);
        user.setName("Test User");
        user.setRole(UserRole.USER);

        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        validToken = JWT.create()
                .withIssuer("auth-api")
                .withSubject(userEmail)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }


    @Nested
    @DisplayName("Check Token in Repository")
    class testCheckTokenInRepository {

        String token = "some-token";

        private Optional<Token> checkToken(String token) {
            return tokenService.checkTokenInRepository(token);
        }

        @Test
        @DisplayName("Should return empty when token is not found")
        void testCheckTokenInRepository_TokenNotFound() {
            //Arrange
            when(tokenRepository.findByTokenValue(token)).thenReturn(Optional.empty());

            // Act
            Optional<Token> result = checkToken(token);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty Optional when token is revoked")
        void testCheckTokenInRepository_TokenRevoked() {
            // Arrange
            Token revokedToken = new Token();
            revokedToken.setRevoked(true);

            when(tokenRepository.findByTokenValue(token)).thenReturn(Optional.of(revokedToken));

            // Act
            Optional<Token> result = checkToken(token);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return token when token is valid")
        void testCheckTokenInRepository_TokenValid() {
            // Arrange
            Token validTokenInRepository = new Token();
            validTokenInRepository.setRevoked(false);

            when(tokenRepository.findByTokenValue(token)).thenReturn(Optional.of(validTokenInRepository));

            // Act
            Optional<Token> result = checkToken(token);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(validTokenInRepository, result.get());
        }
    }

    @Nested
    @DisplayName("Generate Token")
    class testGenerateTokenMethod {

        private String generateToken(User user) {
            return tokenService.generateToken(user, 60);
        }

        private String resultValidateToken() {
            return tokenService.validateToken(validToken);
        }

        @Test
        @DisplayName("Should return a valid token when user is valid")
        void generateToken_ShouldReturnToken_WhenUserIsValid() {
            // Act
            String token = generateToken(user);

            // Assert
            assertNotNull(token);
            assertTrue(token.startsWith("eyJ"));
        }

        @Test
        @DisplayName("Should return null when token is not found in database")
        void testValidateTokenNotFoundInDatabase() {
            // Arrange
            when(ipBlockingService.getRealClientIP()).thenReturn(ipAddress);
            when(request.getHeader("User-Agent")).thenReturn(userAgent);
            when(tokenRepository.findByTokenValue(validToken)).thenReturn(Optional.empty());

            // Act
            String result = resultValidateToken();

            // Assert
            assertNull(result);
            verify(tokenRepository, times(1)).findByTokenValue(validToken);
        }

        @Test
        @DisplayName("Should return user email when token is found in the database")
        void testValidateTokenFoundInDatabase() {
            // Arrange
            Token expectedToken = new Token();

            when(ipBlockingService.getRealClientIP()).thenReturn(ipAddress);
            when(request.getHeader("User-Agent")).thenReturn(userAgent);
            when(tokenRepository.findByTokenValue(validToken)).thenReturn(Optional.of(expectedToken));

            // Act
            String result = resultValidateToken();

            // Assert
            assertEquals(userEmail, result);
            verify(tokenRepository, times(1)).findByTokenValue(validToken);
        }

        @Test
        @DisplayName("Should include correct claims in the generated token")
        void generateToken_ShouldIncludeCorrectClaims() {
            // Act
            String token = generateToken(user);
            DecodedJWT decodedJWT = JWT.decode(token);

            // Assert
            assertEquals("auth-api", decodedJWT.getIssuer());
            assertEquals("test@example.com", decodedJWT.getSubject());
            assertEquals("Test User", decodedJWT.getClaim("Name").asString());
            assertEquals("USER", decodedJWT.getAudience().getFirst());
        }

        @Test
        @DisplayName("Should set expiration date in the generated token")
        void generateToken_ShouldSetExpirationDate() {
            // Act
            String token = generateToken(user);
            DecodedJWT decodedJWT = JWT.decode(token);

            // Assert
            assertNotNull(decodedJWT.getExpiresAt());
        }

        @Test
        @DisplayName("Should log error when JWT creation fails")
        void generateToken_ShouldLogError_WhenJWTCreationFails() {
            // Act & Assert
            TokenService tokenServiceSpy = spy(tokenService);
            doThrow(JWTCreationException.class).when(tokenServiceSpy).getAlgorithm();

            assertThrows(TokenGenerationException.class, () -> tokenServiceSpy.generateToken(user, 60));
        }
    }

    @Nested
    @DisplayName("Tests for generate and refresh token methods")
    class testGenerateAndRefreshTokenMethods {
        @Test
        @DisplayName("Should generate a valid access token")
        void testGenerateAccessToken() {
            // Act
            tokenService.expirationToken = 10;
            String token = tokenService.generateAccessToken(user);

            // Assert
            assertNotNull(token);
            assertNotNull(token);
            assertDoesNotThrow(() -> JWT.require(Algorithm.RSA256(publicKey, privateKey)).build().verify(token));
        }

        @Test
        @DisplayName("Should generate a valid refresh token")
        void testGenerateRefreshToken() {
            // Act
            tokenService.refreshTokenExpiration = 30;
            String refreshToken = tokenService.generateRefreshToken(user);

            // Assert
            assertNotNull(refreshToken);
            assertDoesNotThrow(() -> JWT.require(Algorithm.RSA256(publicKey, privateKey)).build().verify(refreshToken));
        }
    }

    @Nested
    @DisplayName("Tests for verifyToken method")
    class testVerifyTokenMethod {
        @Test
        @DisplayName("Should return user email for a valid token")
        void testValidateTokenWithValidToken() {
            // Arrange
            Token validTokenObj = new Token();
            validTokenObj.setUser(user);

            when(ipBlockingService.getRealClientIP()).thenReturn(ipAddress);
            when(request.getHeader("User-Agent")).thenReturn(userAgent);
            when(ipBlockingService.isBlocked(ipAddress)).thenReturn(false);
            when(tokenRepository.findByTokenValue(validToken)).thenReturn(Optional.of(validTokenObj));

            // Act
            String result = tokenService.validateToken(validToken);

            // Assert
            assertEquals(userEmail, result);
            verify(ipBlockingService, times(0)).registerFailedAttempt(ipAddress, userAgent);
        }

        @Test
        @DisplayName("Should return null and register a failed attempt for a malformed token")
        void testValidateTokenWithMalformedToken() {
            // Arrange
            String malformedToken = "malformed.token";

            when(ipBlockingService.getRealClientIP()).thenReturn(ipAddress);
            when(request.getHeader("User-Agent")).thenReturn(userAgent);

            // Act
            String result = tokenService.validateToken(malformedToken);

            // Assert
            assertNull(result);
            verify(ipBlockingService, times(1)).registerFailedAttempt(ipAddress, userAgent);
        }
    }

    @Nested
    @DisplayName("Tests for validateToken method")
    class testValidateTokenMethod {
        @Test
        void testValidateTokenBlockedIP() {
            // Arrange
            String validTokenTokenBlockedIP = "valid.token";

            when(ipBlockingService.getRealClientIP()).thenReturn(ipAddress);
            when(request.getHeader("User-Agent")).thenReturn(userAgent);
            when(ipBlockingService.isBlocked("127.0.0.1")).thenReturn(true);

            // Act
            String result = tokenService.validateToken(validTokenTokenBlockedIP);

            // Assert
            assertNull(result);
            verify(ipBlockingService, times(1)).isBlocked(ipAddress);
        }
    }
}