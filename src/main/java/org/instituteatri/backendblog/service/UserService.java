package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.ChangePasswordRequestDTO;
import org.instituteatri.backendblog.dto.request.UpdateUserRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.dto.response.UserResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.InvalidOldPasswordException;
import org.instituteatri.backendblog.infrastructure.exceptions.UserNotFoundException;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.strategy.interfaces.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final AuthenticationTokenManager authTokenManager;
    private final AuthenticationValidationStrategy authValidationStrategy;
    private final PasswordValidationStrategy passwordValidationStrategy;
    private final UserIdValidationStrategy userIdValidationStrategy;
    private final EmailAlreadyValidationStrategy emailAlreadyValidationStrategy;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<UserResponseDTO>> processFindAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDTO> response = new ArrayList<>();

        users.forEach(x -> response.add(modelMapper.map(x, UserResponseDTO.class)));
        return ResponseEntity.ok(response);
    }

    public UserResponseDTO findById(String id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<PostResponseDTO> findPostsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return user.getPosts().stream()
                .map(post -> modelMapper.map(post, PostResponseDTO.class))
                .toList();
    }

    public ResponseEntity<Void> processDeleteUser(String userId) {

        User existingUser = findUserByIdOrThrow(userId);

        deletePostsByUserId(userId);

        userRepository.delete(existingUser);

        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<TokenResponseDTO> processUpdateUser(String userId, UpdateUserRequestDTO updatedUserDto, Authentication authentication) {

        User existingUser = findUserByIdOrThrow(userId);

        authValidationStrategy.validate(authentication);

        passwordValidationStrategy.validate(updatedUserDto.password(), updatedUserDto.confirmPassword());

        userIdValidationStrategy.validate(authentication, userId);

        validateEmailUpdate(existingUser, updatedUserDto);
        updateUserProperties(existingUser, updatedUserDto);
        userRepository.save(existingUser);

        authTokenManager.revokeAllUserTokens(existingUser);

        return ResponseEntity.ok(authTokenManager.generateTokenResponse(existingUser));
    }


    @Transactional
    public ResponseEntity<Void> processChangePassword(ChangePasswordRequestDTO changePasswordRequestDTO, Authentication authentication) {

        authValidationStrategy.validate(authentication);

        User user = (User) authentication.getPrincipal();
        User existingUser = findUserByIdOrThrow(user.getId());

        if (!passwordEncoder.matches(changePasswordRequestDTO.oldPassword(), existingUser.getPassword())) {
            throw new InvalidOldPasswordException();
        }

        existingUser.setPassword(passwordEncoder.encode(changePasswordRequestDTO.newPassword()));
        userRepository.save(existingUser);

        return ResponseEntity.noContent().build();
    }

    private void deletePostsByUserId(String userId) {
        List<Post> posts = postRepository.findPostsById(userId);
        postRepository.deleteAll(posts);
    }

    private User findUserByIdOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void updateUserProperties(User existingUser, UpdateUserRequestDTO updatedUserDto) {
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

    private void validateEmailUpdate(User existingUser, UpdateUserRequestDTO updatedUserDto) {
        String newEmail = updatedUserDto.email();

        emailAlreadyValidationStrategy.validate(
                existingUser.getEmail(),
                newEmail,
                existingUser.getId()
        );
    }


    private void updatePassword(User existingUser, String newPassword) {
        if (newPassword != null && !passwordEncoder.matches(newPassword, existingUser.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encryptedPassword);
        }
    }
}