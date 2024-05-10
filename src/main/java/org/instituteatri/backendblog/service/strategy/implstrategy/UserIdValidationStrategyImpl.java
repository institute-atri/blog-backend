package org.instituteatri.backendblog.service.strategy.implstrategy;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.instituteatri.backendblog.service.strategy.interfaces.UserIdValidationStrategy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserIdValidationStrategyImpl implements UserIdValidationStrategy {
    @Override
    public void validate(Authentication authentication, String userId) {
        String authenticatedUserId = ((User) authentication.getPrincipal()).getId();
        if (!userId.equals(authenticatedUserId)) {
            throw new UserAccessDeniedException();
        }
    }
}
