package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.domain.token.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    User user = new User();
    List<Token> expectedTokens = List.of(
            new Token("1", "tokenValue1", TokenType.BEARER, user, false, false),
            new Token("2", "tokenValue2", TokenType.BEARER, user, false, false)
    );

    @BeforeEach
    void setUp() {
        tokenRepository.saveAll(expectedTokens);
    }

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find all valid tokens for user successfully")
    void shouldFindAllValidTokensForUser() {
        // Act
        List<Token> actualTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // Assert
        assertNotNull(actualTokens);
        assertEquals(expectedTokens.size(), actualTokens.size());

        for (Token expectedToken : expectedTokens) {
            assertTrue(actualTokens.contains(expectedToken));
        }
    }

    @Test
    @DisplayName("Should return empty list when no valid tokens found for user")
    void shouldReturnEmptyListWhenNoValidTokensFoundForUser() {
        // Act
        List<Token> actualTokens = tokenRepository.findAllValidTokenByUser("nonExistentUserId");

        // Assert
        assertNotNull(actualTokens);
        assertTrue(actualTokens.isEmpty());
    }

    @Test
    @DisplayName("Should find only non-expired and non-revoked tokens for user when some exist")
    void shouldFindOnlyNonExpiredAndNonRevokedTokensForUserWhenSomeExist() {
        // Arrange
        List<Token> filteredTokens = expectedTokens.stream()
                .filter(token -> !token.isExpired() && !token.isRevoked())
                .toList();

        // Act
        List<Token> actualTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // Filter the tokens
        List<Token> filteredActualTokens = actualTokens.stream()
                .filter(token -> !token.isExpired() && !token.isRevoked())
                .toList();

        // Assert
        assertNotNull(actualTokens);
        assertThat(filteredActualTokens).hasSameElementsAs(filteredTokens);
    }

    @Test
    @DisplayName("Should find only expired or revoked tokens for user when some exist")
    void shouldFindOnlyExpiredOrRevokedTokensForUserWhenSomeExist() {
        // Arrange
        List<Token> filteredTokens = expectedTokens.stream()
                .filter(token -> token.isExpired() || token.isRevoked())
                .toList();

        // Act
        List<Token> actualTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        // Filter the tokens
        List<Token> filteredActualTokens = actualTokens.stream()
                .filter(token -> token.isExpired() || token.isRevoked())
                .toList();

        // Assert
        assertNotNull(actualTokens);
        assertThat(filteredActualTokens).hasSameElementsAs(filteredTokens);
    }

    @Test
    @DisplayName("Should find all tokens for user")
    void shouldFindAllTokensForUser() {
        // Act
        List<Token> actualTokens = tokenRepository.findAllByUserId(user.getId());

        // Assert
        assertNotNull(actualTokens);
        assertEquals(expectedTokens.size(), actualTokens.size());
    }

    @Test
    @DisplayName("Should not find any tokens for non-existent user")
    void shouldNotFindAnyTokensForNonExistentUser() {
        // Arrange

        // Act
        List<Token> actualTokens = tokenRepository.findAllByUserId("non-existent-user-id");

        // Assert
        assertNotNull(actualTokens);
        assertTrue(actualTokens.isEmpty());
    }

    @Test
    @DisplayName("Should accurately find token by token value and verify all token attributes")
    void shouldFindTokenByTokenValueAndVerifyAttributes() {
        // Arrange
        String tokenValue = "tokenValue1";
        Token expectedToken = new Token("1", tokenValue, TokenType.BEARER, user, false, false);
        Token anotherToken = new Token("2", "tokenValue2", TokenType.BEARER, user, false, false);
        tokenRepository.saveAll(List.of(expectedToken, anotherToken));

        // Act
        Optional<Token> foundTokenOptional = tokenRepository.findByTokenValue(tokenValue);

        // Assert
        assertTrue(foundTokenOptional.isPresent(), "Token should be found by its value");
        foundTokenOptional.ifPresent(token -> {
            assertEquals(expectedToken.getId(), token.getId(), "Token ID should match");
            assertEquals(expectedToken.getTokenValue(), token.getTokenValue(), "Token value should match");
            assertEquals(expectedToken.getTokenType(), token.getTokenType(), "Token type should match");
            assertEquals(expectedToken.getUser(), token.getUser(), "Token user should match");
            assertFalse(token.isExpired(), "Token should not be expired");
            assertFalse(token.isRevoked(), "Token should not be revoked");
        });
    }

    @Test
    @DisplayName("Should not find token by invalid token value and verify no token is returned")
    void shouldNotFindTokenByInvalidTokenValueAndVerifyNoTokenIsReturned() {
        // Arrange
        String invalidTokenValue = "invalidTokenValue";

        // Act
        Optional<Token> foundTokenOptional = tokenRepository.findByTokenValue(invalidTokenValue);

        // Assert
        assertFalse(foundTokenOptional.isPresent(), "Token should not be found with an invalid value");
    }
}