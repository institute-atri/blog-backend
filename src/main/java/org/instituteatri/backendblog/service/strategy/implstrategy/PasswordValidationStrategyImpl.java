package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.infrastructure.exceptions.PasswordsNotMatchException;
import org.instituteatri.backendblog.service.strategy.interfaces.PasswordValidationStrategy;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidationStrategyImpl implements PasswordValidationStrategy {
    @Override
    public void validate(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordsNotMatchException();
        }
    }
}
