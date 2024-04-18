package org.instituteatri.backendblog.service.helpers.helpUser;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class HelperComponentAuthenticationUser {

    public void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }

    public String getAuthenticatedUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }

    public void validateUserAccess(String id, String authenticatedUserId) {
        if (!id.equals(authenticatedUserId)) {
            throw new DomainAccessDeniedException();
        }
    }
}
