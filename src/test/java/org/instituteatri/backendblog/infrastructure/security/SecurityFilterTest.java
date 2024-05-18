package org.instituteatri.backendblog.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.instituteatri.backendblog.domain.token.Token;
import org.instituteatri.backendblog.infrastructure.exceptions.TokenInvalidException;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter mockWriter;

    @Mock
    private Token mockToken;

    @Mock
    private UserDetails mockUserDetails;

    @InjectMocks
    private SecurityFilter securityFilter;

    private final String invalidToken = "Invalid token";
    private final String validToken = "valid token";
    private final String email = "user@example.com";


    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Test Do Filter Internal parameters")
    class testDoFilterInternalParameters {
        @Test
        @DisplayName("Should throw NullPointerException when request is null")
        void testDoFilterInternal_withNullRequest() {
            // Act
            request = null;

            // Assert
            assertThrows(NullPointerException.class, () ->
                    securityFilter.doFilterInternal(request, response, filterChain)
            );
        }

        @Test
        @DisplayName("Should throw NullPointerException when response is null")
        void testDoFilterInternal_withNullResponse() {
            // Act
            response = null;

            // Assert
            assertThrows(NullPointerException.class, () ->
                    securityFilter.doFilterInternal(request, response, filterChain)
            );
        }

        @Test
        @DisplayName("Should throw NullPointerException when filterChain is null")
        void testDoFilterInternal_withNullFilterChain() {
            // Act
            filterChain = null;

            // Assert
            assertThrows(NullPointerException.class, () ->
                    securityFilter.doFilterInternal(request, response, filterChain)
            );
        }

        @Test
        @DisplayName("Should not throw exception when request is not null")
        void testDoFilterInternal_withNonNullRequest() {
            assertDoesNotThrow(() ->
                    securityFilter.doFilterInternal(request, response, filterChain)
            );
        }

        @Test
        @DisplayName("Should not throw exception when response is not null")
        void testDoFilterInternal_withNonNullResponse() {
            assertDoesNotThrow(() ->
                    securityFilter.doFilterInternal(request, response, filterChain)
            );
        }

        @Test
        @DisplayName("Should not throw exception when filterChain is not null")
        void testDoFilterInternal_withNonNullFilterChain() {
            assertDoesNotThrow(() ->
                    securityFilter.doFilterInternal(request, response, filterChain)
            );
        }
    }

    @Nested
    @DisplayName("Test Do Filter Internal method")
    class testDoFilterInternalMethod {
        @Test
        @DisplayName("Should proceed with valid token")
        void testDoFilterInternal_withValidToken() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenService.validateToken(validToken)).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(mockUserDetails);
            when(tokenRepository.findByTokenValue(validToken)).thenReturn(Optional.of(mockToken));
            when(mockToken.isExpired()).thenReturn(false);
            when(mockToken.isRevoked()).thenReturn(false);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, never()).doFilter(request, response);
            verify(tokenService).validateToken(validToken);
            verify(userRepository).findByEmail(email);
            verify(tokenRepository).findByTokenValue(validToken);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            verify(mockToken, times(1)).isExpired();
            verify(mockToken, times(1)).isRevoked();
        }

        @Test
        @DisplayName("Should handle authentication for valid token")
        void testDoFilterInternal_ValidToken() throws Exception {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenService.validateToken(validToken)).thenReturn(email);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(tokenService, times(1)).validateToken(validToken);
            verify(filterChain, times(0)).doFilter(request, response);
        }

        @Test
        @DisplayName("Should not handle authentication for null token")
        void testDoFilterInternal_NullToken() throws Exception {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(tokenService, never()).validateToken(anyString());
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @Test
        @DisplayName("Should propagate exception when error occurs")
        void testDoFilterInternal_Exception() {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
            when(tokenService.validateToken(invalidToken)).thenThrow(new RuntimeException("Token validation failed"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> securityFilter.doFilterInternal(request, response, filterChain));
        }

        @Test
        @DisplayName("Should proceed without token")
        void testDoFilterInternal_withoutToken() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn(null);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain).doFilter(request, response);
            verify(tokenService, never()).validateToken(any());
            verify(userRepository, never()).findByEmail(any());
            verify(tokenRepository, never()).findByTokenValue(any());
        }

        @Test
        @DisplayName("Should not filter request and set authentication when token is invalid")
        void testDoFilterInternal_withInvalidToken() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
            when(tokenService.validateToken(invalidToken)).thenReturn(null);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, never()).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    @DisplayName("Test Handle Authentication method")
    class testHandleAuthenticationMethod {
        @Test
        @DisplayName("Should handle invalid token")
        void testHandleAuthentication_withInvalidToken_ShouldCallHandleInvalidToken() throws IOException {
            // Arrange
            when(tokenService.validateToken(invalidToken)).thenThrow(
                    new TokenInvalidException(invalidToken)
            );
            when(response.getWriter()).thenReturn(mockWriter);

            // Act
            securityFilter.handleAuthentication(invalidToken, response);

            // Assert
            verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
            verify(mockWriter).write("Token is invalid:" + invalidToken);
        }
    }

    @Nested
    @DisplayName("Test Token Is Revoked And Expired")
    class testTokenIsRevokedAndExpired {
        @Test
        @DisplayName("Should not proceed with valid token when user not found")
        void testDoFilterInternal_ValidToken_UserNotFound() throws ServletException, IOException {
            // Arrange
            when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
            when(tokenService.validateToken(validToken)).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(null);
            when(tokenRepository.findByTokenValue(validToken)).thenReturn(Optional.of(mockToken));
            when(mockToken.isExpired()).thenReturn(false);
            when(mockToken.isRevoked()).thenReturn(false);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, never()).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Should not proceed with expired token")
        void testDoFilterInternal_withExpiredToken() throws ServletException, IOException {
            // Arrange
            String expiredToken = "expiredToken";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
            when(tokenService.validateToken(expiredToken)).thenReturn(email);
            when(userRepository.findByEmail(email)).thenReturn(mockUserDetails);
            when(tokenRepository.findByTokenValue(expiredToken)).thenReturn(Optional.of(mockToken));
            when(mockToken.isExpired()).thenReturn(true);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, never()).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Should not proceed with revoked token")
        void testDoFilterInternal_withRevokedToken() throws ServletException, IOException {
            // Arrange
            String revokedToken = "revokedToken";

            when(request.getHeader("Authorization")).thenReturn("Bearer " + revokedToken);
            when(tokenService.validateToken(revokedToken)).thenReturn(email);
            when(tokenRepository.findByTokenValue(revokedToken)).thenReturn(Optional.of(mockToken));
            when(mockToken.isExpired()).thenReturn(false);
            when(mockToken.isRevoked()).thenReturn(true);

            // Act
            securityFilter.doFilterInternal(request, response, filterChain);

            // Assert
            verify(filterChain, never()).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    @DisplayName("Test Is Token Valid method")
    class testIsTokenValidMethod {
        @Test
        @DisplayName("Token should be invalid if expired")
        public void testIsTokenValid_whenTokenRepositoryReturnsExpiredToken_thenReturnsFalse() {
            // Arrange
            when(tokenRepository.findByTokenValue("expiredToken")).thenReturn(Optional.of(mockToken));
            when(mockToken.isExpired()).thenReturn(true);

            // Act
            boolean result = securityFilter.isTokenValid("expiredToken");

            // Assert
            assertFalse(result, "The token should be invalid because it is expired");
            verify(tokenRepository, times(1)).findByTokenValue("expiredToken");
            verify(mockToken, times(1)).isExpired();
        }

        @Test
        @DisplayName("Token should be invalid if revoked")
        public void testIsTokenValid_whenTokenRepositoryReturnsRevokedToken_thenReturnsFalse() {
            // Arrange
            when(tokenRepository.findByTokenValue("revokedToken")).thenReturn(Optional.of(mockToken));
            when(mockToken.isRevoked()).thenReturn(true);

            // Act
            boolean result = securityFilter.isTokenValid("revokedToken");

            // Assert
            assertFalse(result, "The token should be invalid because it is revoked");
            verify(tokenRepository, times(1)).findByTokenValue("revokedToken");
            verify(mockToken, times(1)).isRevoked();
        }

        @Test
        @DisplayName("Token should be valid if not expired and not revoked")
        public void testIsTokenValid_whenTokenRepositoryReturnsValidToken_thenReturnsTrue() {
            // Arrange
            when(tokenRepository.findByTokenValue(validToken)).thenReturn(Optional.of(mockToken));
            when(mockToken.isExpired()).thenReturn(false);
            when(mockToken.isRevoked()).thenReturn(false);

            // Act
            boolean result = securityFilter.isTokenValid(validToken);

            // Assert
            assertTrue(result, "The token should be valid because it is not expired and not revoked");
            verify(tokenRepository, times(1)).findByTokenValue(validToken);
            verify(mockToken, times(1)).isExpired();
            verify(mockToken, times(1)).isRevoked();
        }
    }
}

