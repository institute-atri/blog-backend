package org.instituteatri.backendblog.service.strategy.interfaces;

public interface PasswordValidationStrategy {
    void validate(String password, String confirmPassword);
}
