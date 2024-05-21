package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.domain.token.TokenType;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenGenerationException;
import org.instituteatri.backendblog.infrastructure.security.TokenService;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenManagerImplTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenManagerImpl tokenManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("123");
    }

    @Nested
    class testGenerateTokenResponseMethod {
        @Test
        void testGenerateTokenResponse_Success() {
            // Arrange
            String genAccessToken = "sampleAccessToken";
            String genRefreshToken = "sampleRefreshToken";
            Token accessToken = new Token();
            Token refreshToken = new Token();
            accessToken.setTokenValue(genAccessToken);
            refreshToken.setTokenValue(genRefreshToken);

            when(tokenService.generateAccessToken(user)).thenReturn(genAccessToken);
            when(tokenService.generateRefreshToken(user)).thenReturn(genRefreshToken);
            when(tokenRepository.findAllByUserId(user.getId())).thenReturn(List.of());
            when(tokenRepository.save(any(Token.class))).thenReturn(accessToken, refreshToken);

            // Act
            TokenResponseDTO response = tokenManager.generateTokenResponse(user);

            // Assert
            assertEquals(genAccessToken, response.token());
            assertEquals(genRefreshToken, response.refreshToken());
            verify(tokenService, times(1)).generateAccessToken(user);
            verify(tokenService, times(1)).generateRefreshToken(user);
            verify(tokenRepository, times(1)).findAllByUserId(user.getId());
            verify(tokenRepository, times(2)).save(any(Token.class));
            verify(userRepository, times(1)).save(user);
        }

        @Test
        void testGenerateTokenResponse_TokenGenerationException() {
            // Arrange
            when(tokenService.generateAccessToken(user)).thenThrow(new TokenGenerationException("Failed to generate access token", null));

            // Act & Assert
            assertThrows(TokenGenerationException.class, () -> tokenManager.generateTokenResponse(user));
        }
    }

    @Nested
    class TestClearTokensMethod {
        @Test
        @DisplayName("Should clear tokens for a user")
        void clearTokens_shouldClearTokensForUser() {
            // Arrange
            List<Token> tokensForUser = List.of(new Token(), new Token());
            when(tokenRepository.findAllByUserId(user.getId())).thenReturn(tokensForUser);

            // Act
            tokenManager.clearTokens(user.getId());

            // Assert
            verify(tokenRepository, times(1)).deleteAll(tokensForUser);
        }

        @Test
        @DisplayName("Should do nothing when there are no tokens for a user")
        void clearTokens_shouldDoNothingWhenNoTokensForUser() {
            // Arrange
            when(tokenRepository.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());

            // Act
            tokenManager.clearTokens(user.getId());

            // Assert
            verify(tokenRepository, never()).deleteAll(any());
        }
    }

    @Nested
    class TestSaveUserTokenMethod {

        String jwtToken = "jwtToken";

        @Test
        @DisplayName("Should save user token")
        void saveUserToken_shouldSaveUserToken() {
            // Arrange
            Token token = Token.builder()
                    .user(user)
                    .tokenValue(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            when(tokenRepository.save(any(Token.class))).thenReturn(token);

            // Act
            Token savedToken = tokenManager.saveUserToken(user, jwtToken);

            // Assert
            assertNotNull(savedToken);
            assertEquals(jwtToken, savedToken.getTokenValue());
            verify(tokenRepository, times(1)).save(any(Token.class));
        }

        @Test
        @DisplayName("Should handle failure to save user token")
        void saveUserToken_shouldHandleFailureToSaveUserToken() {
            // Arrange
            when(tokenRepository.save(any(Token.class))).thenThrow(new TokenGenerationException("Failed to save token", null));

            // Act & Assert
            assertThrows(TokenGenerationException.class, () -> tokenManager.saveUserToken(user, jwtToken));
        }
    }

    @Nested
    class TestRevokeAllUserTokensMethod {

        @Captor
        ArgumentCaptor<List<Token>> tokenListCaptor;

        List<Token> tokens = List.of(
                Token.builder().expired(false).revoked(false).build(),
                Token.builder().expired(false).revoked(false).build()
        );

        @Test
        @DisplayName("Should revoke all user tokens")
        void revokeAllUserTokens_shouldRevokeAllUserTokens() {
            // Arrange
            when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(tokens);

            // Act
            tokenManager.revokeAllUserTokens(user);

            // Assert
            verify(tokenRepository, times(1)).saveAll(tokenListCaptor.capture());
            List<Token> capturedTokens = tokenListCaptor.getValue();
            assertEquals(2, capturedTokens.size());
            capturedTokens.forEach(token -> {
                assertTrue(token.isExpired());
                assertTrue(token.isRevoked());
            });
        }

        @Test
        @DisplayName("Should handle failure to revoke user tokens")
        void revokeAllUserTokens_shouldHandleFailureToRevokeUserTokens() {
            // Arrange
            when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(tokens);
            doThrow(new TokenGenerationException("Failed to revoke tokens", null)).when(tokenRepository).saveAll(anyList());

            // Act & Assert
            assertThrows(TokenGenerationException.class, () -> tokenManager.revokeAllUserTokens(user));
        }

        @Test
        @DisplayName("Should do nothing when no valid tokens to revoke")
        void revokeAllUserTokens_shouldDoNothingWhenNoValidTokensToRevoke() {
            // Arrange
            when(tokenRepository.findAllValidTokenByUser(user.getId())).thenReturn(Collections.emptyList());

            // Act
            tokenManager.revokeAllUserTokens(user);

            // Assert
            verify(tokenRepository, never()).saveAll(anyList());
        }
    }
}