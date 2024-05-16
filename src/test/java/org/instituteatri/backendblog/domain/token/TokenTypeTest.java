package org.instituteatri.backendblog.domain.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTypeTest {

    @Test
    public void testTokenTypeToString() {
        assertEquals("BEARER", TokenType.BEARER.toString());
        assertEquals("BASIC", TokenType.BASIC.toString());
    }

    @Test
    public void testStringToTokenType() {
        assertEquals(TokenType.BEARER, TokenType.valueOf("BEARER"));
        assertEquals(TokenType.BASIC, TokenType.valueOf("BASIC"));
    }
}