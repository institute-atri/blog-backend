package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationValidationStrategyImplTest {

    private AuthenticationValidationStrategyImpl strategy;

    @BeforeEach
    public void setUp() {
        strategy = new AuthenticationValidationStrategyImpl();
    }

    @Test
    @DisplayName("Should not throw exception when user is authenticated")
    void validate_shouldNotThrowExceptionWhenUserIsAuthenticated() {
        // Arrange
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", "password", authorities);

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(authentication));
    }

    @Test
    @DisplayName("Should not throw exception when Authentication is not null and authenticated")
    public void validate_whenAuthenticationIsNotNullAndAuthenticated_shouldNotThrowException() {
        // Arrange
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", "password", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(authentication));
    }

    @Test
    @DisplayName("Should throw NotAuthenticatedException when Authentication is null")
    public void validate_whenAuthenticationIsNull_shouldThrowNotAuthenticatedException() {
        // Act & Assert
        assertThrows(NotAuthenticatedException.class, () -> strategy.validate(null));
    }

    @Test
    @DisplayName("Should throw NotAuthenticatedException when Authentication is not authenticated")
    public void validate_whenAuthenticationIsNotAuthenticated_shouldThrowNotAuthenticatedException() {
        // Arrange
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", "password", authorities);
        authentication.setAuthenticated(false);

        // Act & Assert
        assertThrows(NotAuthenticatedException.class, () -> strategy.validate(authentication));
    }

    @Test
    @DisplayName("Should throw NotAuthenticatedException when Security Context Authentication is Null")
    public void validate_whenSecurityContextAuthenticationIsNull_shouldThrowNotAuthenticatedException() {
        // Arrange
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(null);
        SecurityContextHolder.setContext(context);

        // Act & Assert
        assertThrows(NotAuthenticatedException.class, () -> strategy.validate(null));
    }
}