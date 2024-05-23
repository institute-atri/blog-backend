package org.instituteatri.backendblog.service;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.ChangePasswordRequestDTO;
import org.instituteatri.backendblog.dto.request.UpdateUserRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.dto.response.UserResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.*;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.strategy.interfaces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthenticationTokenManager authTokenManager;

    @Mock
    private AuthenticationValidationStrategy authValidationStrategy;

    @Mock
    private PasswordValidationStrategy passwordValidationStrategy;

    @Mock
    private UserIdValidationStrategy userIdValidationStrategy;

    @Mock
    private EmailAlreadyValidationStrategy emailAlreadyValidationStrategy;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private UpdateUserRequestDTO updatedUserDto;

    @InjectMocks
    private UserService userService;

    private User existingUser;

    private final String userId = "123";


    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(userId);
        modelMapper = new ModelMapper();
        userService = new UserService(userRepository, passwordEncoder, postRepository, authTokenManager, authValidationStrategy, passwordValidationStrategy, userIdValidationStrategy, emailAlreadyValidationStrategy, modelMapper);
    }


    @Nested
    @DisplayName("Test Update Field Method")
    class testUpdateFieldMethod {
        @Test
        @DisplayName("updateField should update the field when the new value is different")
        void updateField_ShouldUpdateField_WhenNewValueIsDifferent() {
            // Arrange
            existingUser.setName("Name");

            // Act
            userService.updateField(existingUser::setName, existingUser.getName(), "NameDif");

            // Assert
            assertEquals("NameDif", existingUser.getName());
        }

        @Test
        @DisplayName("updateField should not update the field when the new value is null")
        void updateField_ShouldNotUpdateField_WhenNewValueIsNull() {
            // Arrange
            existingUser.setName("Name");

            // Act
            userService.updateField(existingUser::setName, existingUser.getName(), null);

            // Assert
            assertEquals("Name", existingUser.getName());
        }

        @Test
        @DisplayName("updateField should not update the field when the new value is the same as the current value")
        void updateField_ShouldNotUpdateField_WhenNewValueIsSameAsCurrentValue() {
            // Arrange
            existingUser.setName("SameName");

            // Act
            userService.updateField(existingUser::setName, existingUser.getName(), "SameName");

            // Assert
            assertEquals("SameName", existingUser.getName());
        }

        @Test
        @DisplayName("updateField should update the field when the current value is null")
        void updateField_ShouldUpdateField_WhenCurrentValueIsNull() {
            // Act
            userService.updateField(existingUser::setName, null, "NewName");

            // Assert
            assertEquals("NewName", existingUser.getName());
        }
    }

    @Nested
    @DisplayName("Test Update User Properties Method")
    class UpdateUserPropertiesMethod {
        @Test
        @DisplayName("updateUserProperties should update all fields when new values are different")
        void updateUserProperties_ShouldUpdateAllFields() {
            // Arrange
            when(updatedUserDto.name()).thenReturn("New Name");
            when(updatedUserDto.lastName()).thenReturn("New LastName");
            when(updatedUserDto.phoneNumber()).thenReturn("1234567890");
            when(updatedUserDto.bio()).thenReturn("New Bio");
            when(updatedUserDto.email()).thenReturn("test@example.com");
            when(updatedUserDto.password()).thenReturn("newPassword");

            existingUser.setName("Old Name");
            existingUser.setLastName("Old LastName");
            existingUser.setPhoneNumber("0987654321");
            existingUser.setBio("Old Bio");
            existingUser.setEmail("oldEmail@example.com");
            existingUser.setPassword("oldPasswordHash");

            when(passwordEncoder.matches("newPassword", "oldPasswordHash")).thenReturn(false);
            when(passwordEncoder.encode("newPassword")).thenReturn("newPasswordHash");

            // Act
            userService.updateUserProperties(existingUser, updatedUserDto);

            // Assert
            assertEquals("New Name", existingUser.getName());
            assertEquals("New LastName", existingUser.getLastName());
            assertEquals("1234567890", existingUser.getPhoneNumber());
            assertEquals("New Bio", existingUser.getBio());
            assertEquals("test@example.com", existingUser.getEmail());
            assertEquals("newPasswordHash", existingUser.getPassword());
        }

        @Test
        @DisplayName("updateUserProperties should not update fields when new values are the same as current values")
        void updateUserProperties_ShouldNotUpdateFields_WhenNewValuesAreSame() {
            // Arrange
            when(updatedUserDto.name()).thenReturn("Name");
            when(updatedUserDto.lastName()).thenReturn("LastName");
            when(updatedUserDto.phoneNumber()).thenReturn("0987654321");
            when(updatedUserDto.bio()).thenReturn("Old Bio");
            when(updatedUserDto.email()).thenReturn("test@example.com");
            when(updatedUserDto.password()).thenReturn("oldPassword");

            existingUser.setName("Name");
            existingUser.setLastName("LastName");
            existingUser.setPhoneNumber("0987654321");
            existingUser.setBio("Old Bio");
            existingUser.setEmail("test@example.com");
            existingUser.setPassword("oldPasswordHash");

            when(passwordEncoder.matches("oldPassword", "oldPasswordHash")).thenReturn(true);

            // Act
            userService.updateUserProperties(existingUser, updatedUserDto);

            // Assert
            assertEquals("Name", existingUser.getName());
            assertEquals("LastName", existingUser.getLastName());
            assertEquals("0987654321", existingUser.getPhoneNumber());
            assertEquals("Old Bio", existingUser.getBio());
            assertEquals("test@example.com", existingUser.getEmail());
            assertEquals("oldPasswordHash", existingUser.getPassword());
        }

        @Test
        @DisplayName("updateUserProperties should not update fields when new values are null")
        void updateUserProperties_ShouldNotUpdateFields_WhenNewValuesAreNull() {
            // Arrange
            when(updatedUserDto.name()).thenReturn(null);
            when(updatedUserDto.lastName()).thenReturn(null);
            when(updatedUserDto.phoneNumber()).thenReturn(null);
            when(updatedUserDto.bio()).thenReturn(null);
            when(updatedUserDto.email()).thenReturn(null);
            when(updatedUserDto.password()).thenReturn(null);

            existingUser.setName("Name");
            existingUser.setLastName("LastName");
            existingUser.setPhoneNumber("0987654321");
            existingUser.setBio("Old Bio");
            existingUser.setEmail("test@example.com");
            existingUser.setPassword("oldPasswordHash");

            // Act
            userService.updateUserProperties(existingUser, updatedUserDto);

            // Assert
            assertEquals("Name", existingUser.getName());
            assertEquals("LastName", existingUser.getLastName());
            assertEquals("0987654321", existingUser.getPhoneNumber());
            assertEquals("Old Bio", existingUser.getBio());
            assertEquals("test@example.com", existingUser.getEmail());
            assertEquals("oldPasswordHash", existingUser.getPassword());
        }
    }

    @Nested
    @DisplayName("Test Get All Users Method")
    class testGetAllUsersMethod {
        @Test
        @DisplayName("Should return all users")
        void processFindAllUsers_ShouldReturnListOfUsers_WhenUsersExist() {
            // Arrange
            User user1 = new User();
            user1.setId("1");
            user1.setName("Name 1");

            User user2 = new User();
            user2.setId("2");
            user2.setName("Name 2");

            when(userRepository.findAll()).thenReturn(List.of(user1, user2));

            // Act
            ResponseEntity<List<UserResponseDTO>> response = userService.processFindAllUsers();

            // Assert
            assertNotNull(response);
            assertEquals(2, Objects.requireNonNull(response.getBody()).size());
            assertEquals("Name 1", response.getBody().getFirst().getName());
            assertEquals("Name 2", response.getBody().get(1).getName());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when no users found")
        void testProcessFindAllUsers_NoUsersFound() {
            // Arrange
            when(userRepository.findAll()).thenReturn(new ArrayList<>());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.processFindAllUsers());
        }
    }

    @Nested
    @DisplayName("Test Get User By Id Method")
    class testGetUserByIdMethod {
        @Test
        @DisplayName("findById Returns user when user exists")
        void findById_UserExists_ReturnsUserResponseDTO() {
            // Arrange
            existingUser.setName("Name");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            // Act
            ResponseEntity<UserResponseDTO> response = userService.findById(userId);

            // Assert
            assertNotNull(response);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals(userId, Objects.requireNonNull(response.getBody()).getId());
            assertEquals("Name", response.getBody().getName());
        }

        @Test
        @DisplayName("findById Throws UserNotFoundException when user does not exist")
        void findById_UserDoesNotExist_ThrowsUserNotFoundException() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        }

        @Test
        @DisplayName("findById Throws UserNotFoundException when user id is null")
        void findById_NullUserId_ThrowsUserNotFoundException() {
            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.findById(null));
        }

        @Test
        @DisplayName("findById Throws UserNotFoundException when user id is empty")
        void findById_EmptyUserId_ThrowsUserNotFoundException() {
            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.findById(""));
        }

        @Test
        @DisplayName("findById Throws UserNotFoundException when user id has invalid format")
        void findById_InvalidUserIdFormat_ThrowsUserNotFoundException() {
            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.findById("invalid-id-format"));
        }
    }

    @Nested
    @DisplayName("Test Find Posts By User Id Method")
    class testFindPostsByUserIdMethod {
        @Test
        @DisplayName("Should return posts by user ID")
        void findPostsByUserId_UserExists_ReturnsListOfPosts() {
            // Arrange
            existingUser.setPosts(List.of(new Post(), new Post()));

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            // Act
            ResponseEntity<List<PostResponseDTO>> response = userService.findPostsByUserId(userId);

            // Assert
            assertNotNull(response);
            assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
        }


        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void findPostsByUserId_UserDoesNotExist_ThrowsUserNotFoundException() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.findPostsByUserId(userId));
        }

        @Test
        @DisplayName("Should return empty list when user has no posts")
        void findPostsByUserId_UserExistsButNoPosts_ReturnsEmptyList() {
            // Arrange
            existingUser.setPosts(List.of());

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            // Act
            ResponseEntity<List<PostResponseDTO>> response = userService.findPostsByUserId(userId);

            // Assert
            assertNotNull(response);
            assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
        }
    }

    @Nested
    @DisplayName("Test Delete User Method")
    class testDeleteUserMethod {

        @Test
        void testProcessDeleteUser_Success() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            doNothing().when(postRepository).deleteAll(anyList());
            doNothing().when(userRepository).delete(existingUser);

            // Act
            ResponseEntity<Void> response = userService.processDeleteUser(userId);

            // Assert
            assertEquals(ResponseEntity.noContent().build(), response);
            verify(userRepository, times(1)).findById(userId);
            verify(postRepository, times(1)).deleteAll(anyList());
            verify(userRepository, times(1)).delete(existingUser);
        }

        @Test
        void testProcessDeleteUser_UserNotFound() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () -> userService.processDeleteUser(userId));
            verify(userRepository, times(1)).findById(userId);
            verify(postRepository, never()).deleteAll(anyList());
            verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        void testProcessDeleteUser_DeletePosts() {
            // Arrange
            List<Post> posts = List.of(new Post(), new Post());

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(postRepository.findPostsById(userId)).thenReturn(posts);
            doNothing().when(postRepository).deleteAll(posts);
            doNothing().when(userRepository).delete(existingUser);

            // Act
            ResponseEntity<Void> response = userService.processDeleteUser(userId);

            // Assert
            assertEquals(ResponseEntity.noContent().build(), response);
            verify(postRepository, times(1)).findPostsById(userId);
            verify(postRepository, times(1)).deleteAll(posts);
        }

        @Test
        void testProcessDeleteUser_NoPostsToDelete() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(postRepository.findPostsById(userId)).thenReturn(List.of());
            doNothing().when(postRepository).deleteAll(anyList());
            doNothing().when(userRepository).delete(existingUser);

            // Act
            ResponseEntity<Void> response = userService.processDeleteUser(userId);

            // Assert
            assertEquals(ResponseEntity.noContent().build(), response);
            verify(postRepository, times(1)).findPostsById(userId);
            verify(postRepository, times(1)).deleteAll(anyList());
        }

        @Test
        void testProcessDeleteUser_ExceptionDuringPostDeletion() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            doThrow(new RuntimeException("Error deleting posts")).when(postRepository).deleteAll(anyList());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.processDeleteUser(userId));
            verify(userRepository, times(1)).findById(userId);
            verify(postRepository, times(1)).deleteAll(anyList());
            verify(userRepository, never()).delete(existingUser);
        }
    }

    @Nested
    @DisplayName("Test Update User Method")
    class testUpdateUserMethod {

        UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO(
                "newName",
                "newLastName",
                "12343",
                "newBio",
                "new@example.com",
                "newPassword123",
                "newPassword123");

        @Test
        @DisplayName("processUpdateUser: Should throw UserNotFoundException when user is not found")
        void processUpdateUser_UserNotFound() {
            // Arrange
            when(userRepository.findById("nonexistentUserId")).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class,
                    () -> userService.processUpdateUser("nonexistentUserId", updateUserRequestDTO, authentication));
        }

        @Test
        @DisplayName("processUpdateUser Success")
        void processUpdateUser_Success() {
            // Arrange
            existingUser.setEmail("oldEmail@example.com");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
            when(authTokenManager.generateTokenResponse(any())).thenReturn(
                    new TokenResponseDTO("token", "refreshToken"));

            // Act
            ResponseEntity<TokenResponseDTO> response = userService.processUpdateUser(userId, updateUserRequestDTO, authentication);

            // Assert
            assertNotNull(response);
            assertEquals("token", Objects.requireNonNull(response.getBody()).token());
            assertEquals("refreshToken", Objects.requireNonNull(response.getBody()).refreshToken());
            verify(userRepository).save(existingUser);
            verify(authTokenManager).revokeAllUserTokens(existingUser);
        }

        @Test
        @DisplayName("processUpdateUser Should throw Invalid Password")
        void processUpdateUser_InvalidPassword() {
            // Arrange
            String newPassword = "newPassword";
            String confirmPassword = "differentPassword";
            UpdateUserRequestDTO updateRequestDTO = new UpdateUserRequestDTO(
                    "newName",
                    "newLastName",
                    "2131231",
                    "newBio",
                    "newEmail",
                    newPassword,
                    confirmPassword);

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            doThrow(new PasswordsNotMatchException()).when(passwordValidationStrategy).validate(newPassword, confirmPassword);

            // Act & Assert
            assertThrows(PasswordsNotMatchException.class, () ->
                    userService.processUpdateUser(userId, updateRequestDTO, authentication)
            );

            verify(userRepository, times(1)).findById(userId);
            verify(passwordValidationStrategy, times(1)).validate(newPassword, confirmPassword);
            verify(authValidationStrategy, times(1)).validate(authentication);
            verify(userIdValidationStrategy, never()).validate(authentication, userId);
            verify(userRepository, never()).save(any(User.class));
            verify(authTokenManager, never()).revokeAllUserTokens(existingUser);
            verify(authTokenManager, never()).generateTokenResponse(existingUser);
        }

        @Test
        @DisplayName("processUpdateUser Should throw Invalid Authentication")
        void processUpdateUser_InvalidAuthentication() {
            // Arrange
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            doThrow(new NotAuthenticatedException())
                    .when(authValidationStrategy)
                    .validate(authentication);

            // Act & Assert
            assertThrows(NotAuthenticatedException.class, () -> userService.processUpdateUser(userId, updateUserRequestDTO, authentication));
        }

        @Test
        @DisplayName("processUpdateUser Should throw Email Already Exists")
        void testUpdateUser_EmailAlreadyExists() {
            // Arrange
            existingUser.setEmail("existing@example.com");

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            doNothing().when(authValidationStrategy).validate(authentication);
            doNothing().when(passwordValidationStrategy).validate(updateUserRequestDTO.password(), updateUserRequestDTO.confirmPassword());
            doNothing().when(userIdValidationStrategy).validate(authentication, userId);

            doThrow(new EmailAlreadyExistsException()).when(userRepository).save(any(User.class));

            // Act & Assert
            assertThrows(EmailAlreadyExistsException.class, () ->
                    userService.processUpdateUser(userId, updateUserRequestDTO, authentication));

            verify(userRepository, times(1)).findById(userId);
            verify(authValidationStrategy, times(1)).validate(authentication);
            verify(passwordValidationStrategy, times(1)).validate(updateUserRequestDTO.password(), updateUserRequestDTO.confirmPassword());
            verify(userIdValidationStrategy, times(1)).validate(authentication, userId);
            verify(userRepository, times(1)).save(any(User.class));
            verify(authTokenManager, never()).revokeAllUserTokens(existingUser);
            verify(authTokenManager, never()).generateTokenResponse(existingUser);
        }
    }

    @Nested
    @DisplayName("Test Change Password Method")
    class testChangePasswordMethod {

        ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO(
                "oldPassword",
                "newPassword"
        );

        String encodedOldPassword = "encodedOldPassword";
        String encodedNewPassword = "encodedNewPassword";
        String newPassword = "newPassword";
        String oldPassword = "oldPassword";

        private void setEncodedOldPassword() {
            existingUser.setPassword(encodedOldPassword);
        }

        @Test
        @DisplayName("processChangePassword should update user password when old password is valid")
        void processChangePassword_Success() {
            // Arrange
            setEncodedOldPassword();

            when(authentication.getPrincipal()).thenReturn(existingUser);
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(oldPassword, encodedOldPassword)).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

            // Act
            ResponseEntity<Void> response = userService.processChangePassword(changePasswordRequestDTO, authentication);

            // Assert
            assertEquals(ResponseEntity.noContent().build(), response);
            verify(userRepository, times(1)).save(existingUser);
            assertEquals(encodedNewPassword, existingUser.getPassword());
        }

        @Test
        @DisplayName("processChangePassword should throw InvalidOldPasswordException when old password is invalid")
        void processChangePassword_InvalidOldPassword() {
            // Arrange
            setEncodedOldPassword();

            when(authentication.getPrincipal()).thenReturn(existingUser);
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(oldPassword, encodedOldPassword)).thenReturn(false);

            // Act & Assert
            assertThrows(InvalidOldPasswordException.class, () ->
                    userService.processChangePassword(changePasswordRequestDTO, authentication));

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("processChangePassword should throw UserNotFoundException when user not found")
        void processChangePassword_UserNotFound() {
            // Arrange
            setEncodedOldPassword();

            when(authentication.getPrincipal()).thenReturn(existingUser);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UserNotFoundException.class, () ->
                    userService.processChangePassword(changePasswordRequestDTO, authentication));

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("processChangePassword should throw NotAuthenticatedException when authentication fails")
        void processChangePassword_AuthValidationFails() {
            // Arrange
            setEncodedOldPassword();

            doThrow(new NotAuthenticatedException()).when(authValidationStrategy).validate(authentication);

            // Act & Assert
            assertThrows(NotAuthenticatedException.class, () ->
                    userService.processChangePassword(changePasswordRequestDTO, authentication));

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("processChangePassword should encode new password")
        void processChangePassword_EncodeNewPassword() {
            // Arrange
            setEncodedOldPassword();

            when(authentication.getPrincipal()).thenReturn(existingUser);
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(oldPassword, encodedOldPassword)).thenReturn(true);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

            // Act
            userService.processChangePassword(changePasswordRequestDTO, authentication);

            // Assert
            verify(passwordEncoder, times(1)).encode(newPassword);
            assertEquals(encodedNewPassword, existingUser.getPassword());
        }


        @Test
        @DisplayName("updatePassword should update password when new password is valid")
        void updatePassword_ShouldUpdatePassword_WhenNewPasswordIsValid() {
            // Arrange
            setEncodedOldPassword();

            when(passwordEncoder.matches(newPassword, existingUser.getPassword())).thenReturn(false);
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

            // Act
            userService.updatePassword(existingUser, newPassword);

            // Assert
            assertEquals(encodedNewPassword, existingUser.getPassword());
            verify(passwordEncoder).encode(newPassword);
        }

        @Test
        @DisplayName("updatePassword should not update password when new password is null")
        void updatePassword_ShouldNotUpdatePassword_WhenNewPasswordIsNull() {
            // Arrange
            setEncodedOldPassword();

            // Act
            userService.updatePassword(existingUser, null);

            // Assert
            assertEquals(encodedOldPassword, existingUser.getPassword());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("updatePassword should not update password when new password matches old password")
        void updatePassword_ShouldNotUpdatePassword_WhenNewPasswordMatchesOldPassword() {
            // Arrange
            setEncodedOldPassword();

            String newPasswordRequestDTO = changePasswordRequestDTO.oldPassword();

            when(passwordEncoder.matches(newPasswordRequestDTO, existingUser.getPassword())).thenReturn(true);

            // Act
            userService.updatePassword(existingUser, newPasswordRequestDTO);

            // Assert
            assertEquals(encodedOldPassword, existingUser.getPassword());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("updatePassword should update password when new password does not match old password")
        void updatePassword_ShouldUpdatePassword_WhenNewPasswordDoesNotMatchOldPassword() {
            // Arrange
            setEncodedOldPassword();

            String newPasswordRequestDTO = changePasswordRequestDTO.newPassword();

            when(passwordEncoder.matches(newPasswordRequestDTO, existingUser.getPassword())).thenReturn(false);
            when(passwordEncoder.encode(newPasswordRequestDTO)).thenReturn(encodedNewPassword);

            // Act
            userService.updatePassword(existingUser, newPasswordRequestDTO);

            // Assert
            assertEquals(encodedNewPassword, existingUser.getPassword());
            verify(passwordEncoder).encode(newPasswordRequestDTO);
        }

        @Test
        @DisplayName("updatePassword should not update password when new password is empty")
        void updatePassword_ShouldNotUpdatePassword_WhenNewPasswordIsEmpty() {
            // Arrange
            setEncodedOldPassword();

            // Act
            userService.updatePassword(existingUser, "");

            // Assert
            assertEquals(encodedOldPassword, existingUser.getPassword());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }
}