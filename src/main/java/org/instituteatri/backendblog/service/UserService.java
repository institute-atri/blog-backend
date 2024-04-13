package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.UserDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.instituteatri.backendblog.infrastructure.exceptions.UserNotFoundException;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.helpers.HelperValidateUser;
import org.instituteatri.backendblog.service.helpers.helpUser.HelperComponentUserUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HelperComponentUserUpdate helperComponentUserUpdate;
    private final HelperValidateUser helperValidateUser;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deleteUser(String id, Authentication authentication) {

        validateAuthentication(authentication);

        String authenticatedUserId = getAuthenticatedUserId(authentication);

        boolean isAdmin = helperValidateUser.isAdmin(authentication);

        if (!isAdmin && !id.equals(authenticatedUserId)) {
            throw new DomainAccessDeniedException();
        }

        findById(id);
        userRepository.deleteById(id);
    }

    public ResponseEntity<Void> processUpdateUser(String id, UserDTO user, Authentication authentication) {
        validateAuthentication(authentication);
        String authenticatedUserId = getAuthenticatedUserId(authentication);
        validateUserAccess(id, authenticatedUserId);
        helperComponentUserUpdate.helperUpdateUserInformation(id, user);
        return ResponseEntity.noContent().build();
    }


    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }

    private String getAuthenticatedUserId(Authentication authentication) {
        return ((User) authentication.getPrincipal()).getId();
    }

    private void validateUserAccess(String id, String authenticatedUserId) {
        if (!id.equals(authenticatedUserId)) {
            throw new DomainAccessDeniedException();
        }
    }
}