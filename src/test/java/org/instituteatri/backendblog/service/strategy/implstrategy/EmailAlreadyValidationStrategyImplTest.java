package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.EmailAlreadyExistsException;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailAlreadyValidationStrategyImplTest {

    @Mock
    private UserRepository userRepository;

    private EmailAlreadyValidationStrategyImpl strategy;

    private final String userIdToExclude = "123";
    private final String existingEmail = "old@example.com";
    private final String newEmail = "new@example.com";
    private final User existingUser = new User();

    @BeforeEach
    void setUp() {
        strategy = new EmailAlreadyValidationStrategyImpl(userRepository);
    }

    @Test
    @DisplayName("Should not throw exception when emails match")
    void validate_shouldNotThrowExceptionWhenEmailsMatch() {
        // Arrange
        String existingEmailMatch = "test@example.com";
        String newEmailMatch = "test@example.com";

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(existingEmailMatch, newEmailMatch, userIdToExclude));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when new email already exists and does not match excluded user id")
    void validate_shouldThrowExceptionWhenNewEmailAlreadyExists() {
        // Arrange
        existingUser.setId("456");
        existingUser.setEmail(newEmail);

        when(userRepository.findByEmail(newEmail)).thenReturn(existingUser);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> strategy.validate(existingEmail, newEmail, userIdToExclude));
    }

    @Test
    @DisplayName("Should not throw exception when new email does not exist")
    void validate_shouldNotThrowExceptionWhenNewEmailDoesNotExist() {
        // Arrange
        when(userRepository.findByEmail(newEmail)).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(existingEmail, newEmail, userIdToExclude));
    }

    @Test
    @DisplayName("Should not throw exception when new email exists but matches excluded user id")
    void validate_shouldNotThrowExceptionWhenNewEmailExistsButMatchesExcludedUserId() {
        // Arrange
        existingUser.setId(userIdToExclude);
        existingUser.setEmail(newEmail);

        when(userRepository.findByEmail(newEmail)).thenReturn(existingUser);

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(existingEmail, newEmail, userIdToExclude));
    }
}