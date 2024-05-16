package org.instituteatri.backendblog.domain.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenTypeTest {

    @Test
    void testTokenTypeToString() {
        assertEquals("BEARER", TokenType.BEARER.toString());
        assertEquals("BASIC", TokenType.BASIC.toString());
    }

    @Test
    void testStringToTokenType() {
        assertEquals(TokenType.BEARER, TokenType.valueOf("BEARER"));
        assertEquals(TokenType.BASIC, TokenType.valueOf("BASIC"));
    }
}