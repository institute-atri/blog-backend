package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.UserDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.UserNotFoundException;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.helpers.helpUser.HelperComponentAuthenticationUser;
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
    private final HelperComponentAuthenticationUser helperAuthenticationUser;

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new UserNotFoundException(id));
    }

    public ResponseEntity<Void> processUpdateUser(String id, UserDTO user, Authentication authentication) {
        helperAuthenticationUser.validateAuthentication(authentication);
        String authenticatedUserId = helperAuthenticationUser.getAuthenticatedUserId(authentication);
        helperAuthenticationUser.validateUserAccess(id, authenticatedUserId);
        helperComponentUserUpdate.helperUpdateUserInformation(id, user);
        return ResponseEntity.noContent().build();
    }

    public void deleteUser(String id) {
        findById(id);
        userRepository.deleteById(id);
    }
}