package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.infrastructure.exceptions.PasswordsNotMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationStrategyImplTest {

    private PasswordValidationStrategyImpl strategy;

    @BeforeEach
    void setUp() {
        strategy = new PasswordValidationStrategyImpl();
    }

    @Test
    @DisplayName("Should not throw exception when passwords match")
    void validate_shouldNotThrowExceptionWhenPasswordsMatch() {
        // Arrange
        String passwordMatch = "password123";
        String confirmPasswordMatch = "password123";

        // Act & Assert
        assertDoesNotThrow(() -> strategy.validate(passwordMatch, confirmPasswordMatch));
    }

    @Test
    @DisplayName("Should throw PasswordsNotMatchException when passwords do not match")
    void validate_shouldThrowExceptionWhenPasswordsDoNotMatch() {
        // Arrange
        String passwordNotMatch = "password123";
        String confirmPasswordNotMatch = "password456";

        // Act & Assert
        assertThrows(PasswordsNotMatchException.class, () -> strategy.validate(passwordNotMatch, confirmPasswordNotMatch));
    }
}