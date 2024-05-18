package org.instituteatri.backendblog.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCreationRateLimiterServiceTest {

    private final UserCreationRateLimiterService rateLimiter = new UserCreationRateLimiterService();

    @Test
    @DisplayName("Should allow user creation within max attempts")
    void testAllowUserCreation_WithinMaxAttempts() {
        // Arrange
        String ipAddress = "127.0.0.1";

        // Act & Assert
        assertTrue(rateLimiter.allowUserCreation(ipAddress));
        assertTrue(rateLimiter.allowUserCreation(ipAddress));
        assertTrue(rateLimiter.allowUserCreation(ipAddress));
    }

    @Test
    @DisplayName("Should not allow user creation after max attempts")
    void testAllowUserCreation_AfterMaxAttempts() {
        // Arrange
        String ipAddress = "127.0.0.2";

        // Act
        assertTrue(rateLimiter.allowUserCreation(ipAddress));
        assertTrue(rateLimiter.allowUserCreation(ipAddress));
        assertTrue(rateLimiter.allowUserCreation(ipAddress));

        // Assert
        assertFalse(rateLimiter.allowUserCreation(ipAddress));
    }
}
