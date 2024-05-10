package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.instituteatri.backendblog.service.strategy.interfaces.AuthenticationValidationStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationValidationStrategyImpl implements AuthenticationValidationStrategy {
    @Override
    public void validate(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }
}
