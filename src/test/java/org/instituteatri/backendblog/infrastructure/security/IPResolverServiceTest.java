package org.instituteatri.backendblog.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IPResolverServiceTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private IPResolverService ipResolverService;

    @Test
    @DisplayName("getRealClientIP should return null when all headers are null")
    void getRealClientIP_shouldReturnNullWhenAllHeadersAreNull() {
        // Act
        String realClientIP = ipResolverService.getRealClientIP();
        // Assert
        assertNull(realClientIP);
    }

    @Test
    @DisplayName("getRealClientIP should return null when all headers are empty")
    void getRealClientIP_shouldReturnNullWhenAllHeadersAreEmpty() {
        // Arrange
        for (String header : IPResolverService.HEADERS_TO_CHECK) {
            when(request.getHeader(header)).thenReturn("");
        }
        // Act
        String realClientIP = ipResolverService.getRealClientIP();
        // Assert
        assertNull(realClientIP);
    }

    @Test
    @DisplayName("getRealClientIP should return null when all headers are unknown")
    void getRealClientIP_shouldReturnNullWhenAllHeadersAreUnknown() {
        // Arrange
        for (String header : IPResolverService.HEADERS_TO_CHECK) {
            when(request.getHeader(header)).thenReturn("unknown");
        }
        // Act
        String realClientIP = ipResolverService.getRealClientIP();
        // Assert
        assertNull(realClientIP);
    }

    @Test
    @DisplayName("getRealClientIP should return IP address from X-Forwarded-For header")
    void getRealClientIP_shouldReturnIPAddressFromXForwardedForHeader() {
        // Arrange
        String expectedIPAddress = "192.168.1.1";
        when(request.getHeader("X-Forwarded-For")).thenReturn(expectedIPAddress);
        // Act
        String realClientIP = ipResolverService.getRealClientIP();
        // Assert
        assertEquals(expectedIPAddress, realClientIP);
    }

    @Test
    @DisplayName("getRealClientIP should return remote address when no valid header is present")
    void getRealClientIP_shouldReturnRemoteAddressWhenNoValidHeaderPresent() {
        // Arrange
        String expectedRemoteAddress = "192.168.1.2";
        when(request.getRemoteAddr()).thenReturn(expectedRemoteAddress);
        // Act
        String realClientIP = ipResolverService.getRealClientIP();
        // Assert
        assertEquals(expectedRemoteAddress, realClientIP);
    }
}
