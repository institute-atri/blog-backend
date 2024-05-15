package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.BlockedIP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class BlockedIPRepositoryTest {

    @Autowired
    private BlockedIPRepository blockedIPRepository;

    @AfterEach
    void tearDown() {
        blockedIPRepository.deleteAll();
    }
    @Test
    @DisplayName("Should find blocked IP by its address")
    void shouldFindBlockedIPByAddress() {
        // Arrange
        BlockedIP blockedIP = new BlockedIP(
                "1",
                "127.0.0.1",
                5,
                "Mozilla",
                Instant.now());

        // Save the blocked IP
        blockedIPRepository.save(blockedIP);

        // Act
        List<BlockedIP> foundBlockedIPs = blockedIPRepository.findByIpAddress("127.0.0.1");

        // Assert
        assertNotNull(foundBlockedIPs);
        assertFalse(foundBlockedIPs.isEmpty());
        assertEquals("127.0.0.1", foundBlockedIPs.getFirst().getIpAddress());
    }

    @Test
    @DisplayName("Should find multiple blocked IPs by their addresses")
    void shouldFindMultipleBlockedIPsByAddresses() {
        // Arrange
        BlockedIP blockedIP1 = new BlockedIP("1", "127.0.0.1", 5, "Mozilla", Instant.now());
        BlockedIP blockedIP2 = new BlockedIP("2", "127.0.0.1", 3, "Chrome", Instant.now());

        // Save the blocked IPs
        blockedIPRepository.saveAll(List.of(blockedIP1, blockedIP2));

        // Act
        List<BlockedIP> foundBlockedIPs = blockedIPRepository.findByIpAddress("127.0.0.1");

        // Assert
        assertNotNull(foundBlockedIPs);
        assertFalse(foundBlockedIPs.isEmpty());
        assertEquals(2, foundBlockedIPs.size());
    }
    @Test
    @DisplayName("Should return empty list when no blocked IP found for address")
    void shouldReturnEmptyListWhenNoBlockedIPFoundForAddress() {
        // Act
        List<BlockedIP> foundBlockedIPs = blockedIPRepository.findByIpAddress("192.168.1.1");

        // Assert
        assertNotNull(foundBlockedIPs);
        assertTrue(foundBlockedIPs.isEmpty());
    }
}