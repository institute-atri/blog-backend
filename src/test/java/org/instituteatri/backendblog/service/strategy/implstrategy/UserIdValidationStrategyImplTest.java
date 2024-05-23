package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserIdValidationStrategyImplTest {

    private UserIdValidationStrategyImpl strategy;

    private User user;
    private final String userId = "123";

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        user.setId(userId);
        when(user.getId()).thenReturn(userId);
        strategy = new UserIdValidationStrategyImpl();
    }

    @Test
    @DisplayName("Should not throw exception when userId matches authenticated user id")
    void validate_shouldNotThrowExceptionWhenUserIdMatchesAuthenticatedUserId() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(authentication, userId));
    }

    @Test
    @DisplayName("Should throw UserAccessDeniedException when userId does not match authenticated user id")
    void validate_shouldThrowExceptionWhenUserIdDoesNotMatchAuthenticatedUserId() {
        // Arrange
        String differentUserId = "456";
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        // Act & Assert
        assertThrows(UserAccessDeniedException.class, () -> strategy.validate(authentication, differentUserId));
    }

    @Test
    @DisplayName("Should throw UserAccessDeniedException when authentication principal is not User")
    void validate_shouldThrowExceptionWhenAuthenticationPrincipalIsNotUser() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken("principal", null);

        // Act & Assert
        assertThrows(ClassCastException.class, () -> strategy.validate(authentication, userId));
    }

}