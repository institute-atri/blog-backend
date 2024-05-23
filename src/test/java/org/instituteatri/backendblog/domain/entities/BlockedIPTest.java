package org.instituteatri.backendblog.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BlockedIPTest {

    BlockedIP blockedIp = new BlockedIP("1", "192.168.1.1", 3, "User Agent", Instant.now());

    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        // Arrange
        BlockedIP blockedIP = new BlockedIP();
        String id = "123";
        String ipAddress = "192.168.1.1";
        int failedAttempts = 3;
        String userAgent = "Test User Agent";
        Instant lastFailedAttemptTimestamp = Instant.now();

        // Act
        blockedIP.setId(id);
        blockedIP.setIpAddress(ipAddress);
        blockedIP.setFailedAttempts(failedAttempts);
        blockedIP.setUserAgent(userAgent);
        blockedIP.setLastFailedAttemptTimestamp(lastFailedAttemptTimestamp);

        // Assert
        assertEquals(id, blockedIP.getId());
        assertEquals(ipAddress, blockedIP.getIpAddress());
        assertEquals(failedAttempts, blockedIP.getFailedAttempts());
        assertEquals(userAgent, blockedIP.getUserAgent());
        assertEquals(lastFailedAttemptTimestamp, blockedIP.getLastFailedAttemptTimestamp());
    }

    @Test
    @DisplayName("Test constructor with arguments")
    void testConstructorWithArguments() {
        // Arrange
        String id = "123";
        String ipAddress = "192.168.1.1";
        int failedAttempts = 3;
        String userAgent = "Test User Agent";
        Instant lastFailedAttemptTimestamp = Instant.now();

        // Act
        BlockedIP blockedIPWithArguments = new BlockedIP(id, ipAddress, failedAttempts, userAgent, lastFailedAttemptTimestamp);

        // Assert
        assertEquals(id, blockedIPWithArguments.getId());
        assertEquals(ipAddress, blockedIPWithArguments.getIpAddress());
        assertEquals(failedAttempts, blockedIPWithArguments.getFailedAttempts());
        assertEquals(userAgent, blockedIPWithArguments.getUserAgent());
        assertEquals(lastFailedAttemptTimestamp, blockedIPWithArguments.getLastFailedAttemptTimestamp());
    }

    @Test
    @DisplayName("Test constructor without arguments")
    void testConstructorWithoutArguments() {
        // Act
        BlockedIP blockedIP = new BlockedIP();

        // Assert
        assertNull(blockedIP.getId());
        assertNull(blockedIP.getIpAddress());
        assertEquals(0, blockedIP.getFailedAttempts());
        assertNull(blockedIP.getUserAgent());
        assertNull(blockedIP.getLastFailedAttemptTimestamp());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        // Arrange
        String expectedString = "BlockedIP(id=1, ipAddress=192.168.1.1, failedAttempts=3, userAgent=User Agent, lastFailedAttemptTimestamp=" + blockedIp.getLastFailedAttemptTimestamp() + ")";

        // Act
        String actualString = blockedIp.toString();

        // Assert
        assertEquals(expectedString, actualString, "toString() should return a correct string representation");
    }

    @Test
    @DisplayName("Test hashCode method")
    void testHashCode() {
        // Arrange
        BlockedIP blockedIPHashCode1 = new BlockedIP("1", "192.168.1.1", 3, "User Agent", Instant.now());
        BlockedIP blockedIPHashCode2 = new BlockedIP("1", "192.168.1.1", 3, "User Agent", Instant.now());

        // Act
        int hashCode1 = blockedIPHashCode1.hashCode();
        int hashCode2 = blockedIPHashCode2.hashCode();

        // Assert
        assertEquals(hashCode1, hashCode2, "hashCode() should generate the same value for equal objects");
    }

    @Test
    @DisplayName("Test equals method with same object")
    void testEqualsWithSameObject() {
        // Act & Assert
        assertEquals(blockedIp, blockedIp, "Object should be equal to itself");
    }

    @Test
    @DisplayName("Test equals method with same values")
    void testEqualsWithSameValues() {
        // Arrange
        BlockedIP blockedIPSameValues1 = new BlockedIP("1", "192.168.1.1", 3, "User Agent", Instant.now());
        BlockedIP blockedIPSameValues2 = new BlockedIP("1", "192.168.1.1", 3, "User Agent", Instant.now());

        // Act & Assert
        assertEquals(blockedIPSameValues1, blockedIPSameValues2, "Objects should be equal when they have the same values");
    }

    @Test
    @DisplayName("Test equals method with different class")
    void testEqualsWithDifferentClass() {
        // Act
        boolean result = !blockedIp.equals(new Object());

        // Assert
        assertTrue(result, "Object should not be equal to a different class");
    }

    @Test
    @DisplayName("Test equals method with null")
    void testEqualsWithNull() {
        // Act & Assert
        assertNotEquals(null, blockedIp, "Object should not be equal to null");
    }

    @Test
    @DisplayName("Test equals method with different id")
    void testEqualsWithDifferentId() {
        // Arrange
        BlockedIP blockedIPDifferentId2 = new BlockedIP("2", "192.168.1.1", 3, "User Agent", Instant.now());

        // Act & Assert
        assertNotEquals(blockedIp, blockedIPDifferentId2, "Objects should not be equal when they have different ids");
    }

    @Test
    @DisplayName("Test equals method with different failedAttempts")
    void testEqualsWithDifferentFailedAttempts() {
        // Arrange
        BlockedIP blockedIPFailedAttempts2 = new BlockedIP("1", "192.168.1.1", 5, "User Agent", Instant.now());

        // Act & Assert
        assertNotEquals(blockedIp, blockedIPFailedAttempts2, "Objects should not be equal when they have different failedAttempts");
    }

    @Test
    @DisplayName("Test equals method with different ipAddress")
    void testEqualsWithDifferentIpAddress() {
        // Arrange
        BlockedIP blockedIPDifferentIpAddress2 = new BlockedIP("1", "192.168.1.2", 3, "User Agent", Instant.now());

        // Act & Assert
        assertNotEquals(blockedIp, blockedIPDifferentIpAddress2, "Objects should not be equal when they have different ipAddress");
    }

    @Test
    @DisplayName("Test equals method with different userAgent")
    void testEqualsWithDifferentUserAgent() {
        // Arrange
        BlockedIP blockedIPUserAgent1 = new BlockedIP("1", "192.168.1.1", 3, "User Agent 1", Instant.now());
        BlockedIP blockedIPUserAgent2 = new BlockedIP("1", "192.168.1.1", 3, "User Agent 2", Instant.now());

        // Act & Assert
        assertNotEquals(blockedIPUserAgent1, blockedIPUserAgent2, "Objects should not be equal when they have different userAgent");
    }

    @Test
    @DisplayName("Test equals method with different lastFailedAttemptTimestamp")
    void testEqualsWithDifferentLastFailedAttemptTimestamp() {
        // Arrange
        Instant now = Instant.now();
        BlockedIP blockedIPTimestamp2 = new BlockedIP("1", "192.168.1.1", 3, "User Agent", now.plusSeconds(10));

        // Act & Assert
        assertNotEquals(blockedIp, blockedIPTimestamp2, "Objects should not be equal when they have different lastFailedAttemptTimestamp");
    }
}
