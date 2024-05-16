package org.instituteatri.backendblog.domain.token;

import org.instituteatri.backendblog.domain.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TokenTest {

    private Token createToken(String id, String tokenValue, TokenType tokenType, User user, boolean revoked, boolean expired) {
        return new Token(id, tokenValue, tokenType, user, revoked, expired);
    }

    private Token createTokenUsingBuilder(User user) {
        return Token.builder()
                .id("id")
                .tokenValue("value")
                .tokenType(TokenType.BEARER)
                .user(user)
                .revoked(false)
                .expired(false)
                .build();
    }

    @Test
    @DisplayName("Test equality for objects built using builder")
    void testEqualityForObjectsBuiltUsingBuilder() {
        Token token1 = createTokenUsingBuilder(new User());
        Token token2 = createTokenUsingBuilder(new User());

        assertEquals(token1, token2, "Method should return true for objects built using builder with same values");
    }

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        Token token = new Token();
        String id = "123";
        String tokenValue = "tokenValue";
        TokenType tokenType = TokenType.BEARER;
        User user = new User();
        boolean revoked = false;
        boolean expired = false;

        token.setId(id);
        token.setTokenValue(tokenValue);
        token.setTokenType(tokenType);
        token.setUser(user);
        token.setRevoked(revoked);
        token.setExpired(expired);

        assertEquals(id, token.getId());
        assertEquals(tokenValue, token.getTokenValue());
        assertEquals(tokenType, token.getTokenType());
        assertEquals(user, token.getUser());
        assertEquals(revoked, token.isRevoked());
        assertEquals(expired, token.isExpired());
    }

    @Test
    @DisplayName("Test constructor with arguments")
    void testConstructorWithArguments() {
        String id = "123";
        String tokenValue = "tokenValue";
        TokenType tokenType = TokenType.BEARER;
        User user = new User();
        boolean revoked = false;
        boolean expired = false;

        Token token = createToken(id, tokenValue, tokenType, user, revoked, expired);

        assertEquals(id, token.getId());
        assertEquals(tokenValue, token.getTokenValue());
        assertEquals(tokenType, token.getTokenType());
        assertEquals(user, token.getUser());
        assertEquals(revoked, token.isRevoked());
        assertEquals(expired, token.isExpired());
    }

    @Test
    @DisplayName("Test hashCode method")
    void testHashCode() {
        Token token1 = createToken("id1", "value1", TokenType.BEARER, new User(), false, false);
        Token token2 = createToken("id1", "value1", TokenType.BEARER, new User(), false, false);

        assertEquals(token1.hashCode(), token2.hashCode(), "hashCode() should generate the same value for equal objects");
    }

    @Nested
    @DisplayName("Test equals method")
    class testBooleanEquals {

        Token token = new Token();
        Token token1 = createToken("id1", "value1", TokenType.BEARER, new User(), false, false);
        Token token2 = createToken("id1", "value1", TokenType.BEARER, new User(), false, false);

        @Test
        @DisplayName("Test equals method")
        void testEquals() {
            assertEquals(token1, token2, "Objects should be equal when they have the same values");
        }

        @Test
        @DisplayName("Test equals method with different id")
        void testEqualsWithDifferentId() {
            Token tokenDifferentId1 = createToken("id1", "value", TokenType.BEARER, new User(), false, false);
            Token tokenDifferentId2 = createToken("id2", "value", TokenType.BEARER, new User(), false, false);

            assertNotEquals(tokenDifferentId1, tokenDifferentId2, "Objects should not be equal when they have different ids");
        }

        @Test
        @DisplayName("Test equals method with different tokenValue")
        void testEqualsWithDifferentTokenValue() {
            Token token1 = createToken("id", "value1", TokenType.BEARER, new User(), false, false);
            Token token2 = createToken("id", "value2", TokenType.BEARER, new User(), false, false);

            assertNotEquals(token1, token2, "Objects should not be equal when they have different token values");
        }

        @Test
        @DisplayName("Test equals method with null")
        void testEqualsWithNull() {
            // Act & Assert
            assertNotEquals(token1, null, "Object should not be equal to null");
        }

        @Test
        void testEqualsWithSameObject() {
            assertEquals(token, token, "A token should be equal to itself.");
        }

        @Test
        void testEqualsWithDifferentClassObject() {
            Object other = new Object();
            assertNotEquals(token, other, "A token should not be equal to an object of a different class.");
        }

        @Test
        void testEqualsWithAllFieldsEqual() {
            assertEquals(token1, token2, "Tokens with all fields equal should be considered equal.");
        }

        @Test
        void testEqualsWithDifferentRevoked() {
            Token DifferentRevoked = createToken("1", "value", TokenType.BEARER, new User(), true, false);
            assertNotEquals(token1, DifferentRevoked, "Tokens with different 'revoked' status should not be considered equal.");
        }

        @Test
        @DisplayName("Test equals method with different values including tokenValue")
        void testEqualsWithDifferentValues() {
            Token token2 = createToken("id2", "value2", TokenType.BEARER, new User(), true, true);

            assertNotEquals(token1, token2, "Objects should not be equal when they have different values");
            assertNotEquals(token1.getTokenValue(), token2.getTokenValue(), "Token values should not be equal");
        }

        @Test
        @DisplayName("Test equals method with different tokenType")
        void testEqualsWithDifferentTokenType() {
            Token token1 = createToken("id", "value", TokenType.BEARER, new User(), false, false);
            Token token2 = createToken("id", "value", TokenType.BASIC, new User(), false, false);

            assertNotEquals(token1, token2, "Objects should not be equal when they have different token types");
        }
    }
}
