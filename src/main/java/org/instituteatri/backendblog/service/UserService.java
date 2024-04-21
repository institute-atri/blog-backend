package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.ChangePasswordDTO;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.dtos.RegisterDTO;
import org.instituteatri.backendblog.dtos.UserDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.*;
import org.instituteatri.backendblog.mappings.UserMapper;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;


    public ResponseEntity<List<UserDTO>> processFindAllUsers() {
        List<User> users = userRepository.findAll();

        return ResponseEntity.ok(users.stream()
                .map(userMapper::toUserDto)
                .toList());
    }

    public UserDTO findById(String id) {
        Optional<User> user = userRepository.findById(id);

        return user.map(userMapper::toUserDto).orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<PostDTO> findPostsByUserId(String userId) {
        UserDTO userDTO = findById(userId);
        return userDTO.postDTOS();
    }

    public ResponseEntity<Void> processDeleteUser(String id) {
        findById(id);
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<Void> processUpdateUser(String id, RegisterDTO user, Authentication authentication) {
        validateAuthentication(authentication);

        if (!user.password().equals(user.confirmPassword())) {
            throw new PasswordsNotMatchException();
        }

        String authenticatedUserId = ((User) authentication.getPrincipal()).getId();

        if (!id.equals(authenticatedUserId)) {
            throw new UserAccessDeniedException();
        }

        performUserUpdate(id, user);

        return ResponseEntity.noContent().build();
    }



    @Transactional
    public ResponseEntity<Void> processChangePassword(ChangePasswordDTO changePasswordDTO, Authentication authentication) {
        validateAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new UserNotFoundException(user.getId()));

        if (!passwordEncoder.matches(changePasswordDTO.oldPassword(), existingUser.getPassword())) {
            throw new InvalidOldPasswordException();
        }

        existingUser.setPassword(passwordEncoder.encode(changePasswordDTO.newPassword()));
        saveUser(existingUser);

        return ResponseEntity.noContent().build();
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }
    }

    private void performUserUpdate(String userId, RegisterDTO updatedUserDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        validateEmailUpdate(existingUser, updatedUserDto);

        updateUserProperties(existingUser, updatedUserDto);

        saveUser(existingUser);
    }
    private void updateUserProperties(User existingUser, RegisterDTO updatedUserDto) {
        updateField(existingUser::setName, existingUser.getName(), updatedUserDto.name());
        updateField(existingUser::setLastName, existingUser.getLastName(), updatedUserDto.lastName());
        updateField(existingUser::setPhoneNumber, existingUser.getPhoneNumber(), updatedUserDto.phoneNumber());
        updateField(existingUser::setBio, existingUser.getBio(), updatedUserDto.bio());
        updateField(existingUser::setEmail, existingUser.getEmail(), updatedUserDto.email());
        updatePassword(existingUser, updatedUserDto.password());
    }

    private <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }

    private void validateEmailUpdate(User existingUser, RegisterDTO updatedUserDto) {
        String newEmail = updatedUserDto.email();

        if (!existingUser.getEmail().equals(newEmail) && checkIfEmailExists(newEmail, existingUser.getId())) {
            throw new EmailAlreadyExistsException();
        }
    }

    private boolean checkIfEmailExists(String email, String userIdToExclude) {
        User user = (User) userRepository.findByEmail(email);
        return user != null && !user.getId().equals(userIdToExclude);
    }

    private void updatePassword(User existingUser, String newPassword) {
        if (newPassword != null && !passwordEncoder.matches(newPassword, existingUser.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encryptedPassword);
        }
    }

    private void saveUser(User user) {
        userRepository.save(user);
    }
}