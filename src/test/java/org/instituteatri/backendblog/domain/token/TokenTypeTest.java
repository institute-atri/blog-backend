package org.instituteatri.backendblog.domain.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTypeTest {

    @Test
    void testTokenType() {
        assertEquals(TokenType.BEARER, TokenType.BEARER);
    }
}