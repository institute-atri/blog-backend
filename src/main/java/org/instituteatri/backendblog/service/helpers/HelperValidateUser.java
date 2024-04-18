package org.instituteatri.backendblog.service.helpers;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class HelperValidateUser {

    public void validateCurrentUser(User currentUser) {
        if (currentUser == null) {
            throw new NotAuthenticatedException();
        }
    }

    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
        return (User) authentication.getPrincipal();
    }
}
