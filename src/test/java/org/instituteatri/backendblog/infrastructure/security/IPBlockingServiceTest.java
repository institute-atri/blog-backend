package org.instituteatri.backendblog.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.instituteatri.backendblog.domain.entities.BlockedIP;
import org.instituteatri.backendblog.repository.BlockedIPRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class IPBlockingServiceTest {

    @Mock
    private BlockedIPRepository blockedIPRepository;

    @Mock
    private IPResolverService ipResolverService;

    @InjectMocks
    private IPBlockingService ipBlockingService;

    String ipAddress = "192.168.1.1";
    String userAgent = "Test User-Agent";
    BlockedIP blockedIP = new BlockedIP();
    List<BlockedIP> blockedIPs = new ArrayList<>();

    @Nested
    @DisplayName("Register Failed Attempt")
    class testRegisterFailedAttempt {

        @Test
        @DisplayName("Should save new IP attempt when IP is not blocked")
        void registerFailedAttempt_whenIpNotBlocked_shouldSaveNewIpAttempt() {
            // Arrange
            when(blockedIPRepository.findByIpAddress(ipAddress)).thenReturn(blockedIPs);

            // Act
            ipBlockingService.registerFailedAttempt(ipAddress, userAgent);

            // Assert
            ArgumentCaptor<BlockedIP> blockedIPArgumentCaptor = ArgumentCaptor.forClass(BlockedIP.class);
            verify(blockedIPRepository).save(blockedIPArgumentCaptor.capture());

            BlockedIP capturedBlockedIP = blockedIPArgumentCaptor.getValue();

            assertEquals(ipAddress, capturedBlockedIP.getIpAddress());
            assertEquals(userAgent, capturedBlockedIP.getUserAgent());
            assertEquals(1, capturedBlockedIP.getFailedAttempts());
            assertNotNull(capturedBlockedIP.getLastFailedAttemptTimestamp());
        }

        @Test
        @DisplayName("Should increment attempt count when IP has multiple failed attempts")
        void registerFailedAttempt_whenIpHasMultipleFailedAttempts_shouldIncrementAttemptCount() {
            // Arrange
            BlockedIP existingBlockedIP = new BlockedIP();
            existingBlockedIP.setIpAddress(ipAddress);
            existingBlockedIP.setFailedAttempts(2);
            existingBlockedIP.setLastFailedAttemptTimestamp(Instant.now());
            blockedIPs = Collections.singletonList(existingBlockedIP);

            when(blockedIPRepository.findByIpAddress(ipAddress)).thenReturn(blockedIPs);

            // Act
            ipBlockingService.registerFailedAttempt(ipAddress, userAgent);

            // Assert
            ArgumentCaptor<BlockedIP> blockedIPArgumentCaptor = ArgumentCaptor.forClass(BlockedIP.class);
            verify(blockedIPRepository).save(blockedIPArgumentCaptor.capture());

            BlockedIP capturedBlockedIP = blockedIPArgumentCaptor.getValue();

            assertEquals(ipAddress, capturedBlockedIP.getIpAddress());
            assertEquals(userAgent, capturedBlockedIP.getUserAgent());
            assertEquals(3, capturedBlockedIP.getFailedAttempts());
            assertNotNull(capturedBlockedIP.getLastFailedAttemptTimestamp());
        }

        @Test
        @DisplayName("Should handle exception when saving blocked IP")
        void registerFailedAttempt_shouldHandleExceptionWhenSavingBlockedIP() {
            // Arrange
            when(blockedIPRepository.findByIpAddress(ipAddress)).thenReturn(blockedIPs);
            doThrow(new RuntimeException("Failed to save blocked IP")).when(blockedIPRepository).save(any());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> ipBlockingService.registerFailedAttempt(ipAddress, userAgent));
        }
    }

    @Nested
    class testIsBlocked {

        @Test
        @DisplayName("Should return true when IP is blocked")
        void isBlocked_whenIpIsBlocked_shouldReturnTrue() {
            // Arrange
            String blockedIpAddress = "192.168.1.5";
            blockedIP.setIpAddress(blockedIpAddress);
            blockedIP.setFailedAttempts(3);
            blockedIPs = Collections.singletonList(blockedIP);

            // Mocking behavior
            when(blockedIPRepository.findByIpAddress(blockedIpAddress)).thenReturn(blockedIPs);

            // Act
            boolean isBlocked = ipBlockingService.isBlocked(blockedIpAddress);

            // Assert
            assertTrue(isBlocked);
        }


        @Test
        @DisplayName("Should return false when IP is not blocked")
        void isBlocked_whenIpIsNotBlocked_shouldReturnFalse() {
            // Arrange
            String notBlockedIpAddress = "192.168.1.2";
            blockedIPs = Collections.emptyList();

            // Mocking behavior
            when(blockedIPRepository.findByIpAddress(notBlockedIpAddress)).thenReturn(blockedIPs);

            // Act
            boolean isBlocked = ipBlockingService.isBlocked(notBlockedIpAddress);

            // Assert
            assertFalse(isBlocked);
        }

        @Test
        @DisplayName("Should return false when IP has not reached blocking threshold")
        void isBlocked_whenIpHasNotReachedBlockingThreshold_shouldReturnFalse() {
            // Arrange
            blockedIP.setIpAddress(ipAddress);
            blockedIP.setFailedAttempts(2);
            blockedIPs = Collections.singletonList(blockedIP);

            when(blockedIPRepository.findByIpAddress(ipAddress)).thenReturn(blockedIPs);

            // Act
            boolean isBlocked = ipBlockingService.isBlocked(ipAddress);

            // Assert
            assertFalse(isBlocked);
        }
    }

    @Test
    @DisplayName("Should return IP address from IPResolverService")
    void getRealClientIP_shouldReturnIPAddressFromIPResolverService() {
        // Arrange
        String expectedIPAddress = "192.168.1.0";
        when(ipResolverService.getRealClientIP()).thenReturn(expectedIPAddress);

        // Act
        String realClientIP = ipBlockingService.getRealClientIP();

        // Assert
        assertEquals(expectedIPAddress, realClientIP);
    }
}