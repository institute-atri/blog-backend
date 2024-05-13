package org.instituteatri.backendblog.controller;

import org.instituteatri.backendblog.dto.request.ChangePasswordRequestDTO;
import org.instituteatri.backendblog.dto.request.UpdateUserRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.dto.response.UserResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.UserNotFoundException;
import org.instituteatri.backendblog.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @Nested
    class getAllUsers {

        @Test
        @DisplayName("Should get all users with success")
        void shouldGetAllUsersWithSuccess() {
            // Arrange
            List<UserResponseDTO> expectedResponse = new ArrayList<>();
            expectedResponse.add(new UserResponseDTO(
                    UUID.randomUUID().toString(),
                    "Name",
                    "LastName",
                    "123456789",
                    "Bio"
            ));
            expectedResponse.add(new UserResponseDTO(
                    UUID.randomUUID().toString(),
                    "Name2",
                    "LastName2",
                    "123456789",
                    "Bio2"
            ));
            when(userService.processFindAllUsers()).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<List<UserResponseDTO>> responseEntity = userController.findAllUsers();

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).processFindAllUsers();
        }

        @Test
        @DisplayName("Should get all users with success when not found")
        void shouldGetAllUsersWithSuccessWhenNotFound() {
            // Arrange
            when(userService.processFindAllUsers()).thenThrow(new UserNotFoundException("No users found"));

            // Act
            Exception exception = assertThrows(UserNotFoundException.class, () -> userController.findAllUsers());

            // Assert
            assertThat(exception.getMessage()).isEqualTo("No users found");
        }
    }

    @Nested
    class getUserById {

        @Test
        @DisplayName("Should get user by id with success")
        void shouldGetUserByIdWithSuccess() {
            // Arrange
            String expectedId = UUID.randomUUID().toString();
            UserResponseDTO expectedResponse = new UserResponseDTO(
                    expectedId,
                    "Name",
                    "LastName",
                    "123456789",
                    "Bio"
            );
            when(userService.findById(expectedId)).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<UserResponseDTO> responseEntity = userController.findByIdUser(expectedId);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).findById(expectedId);
        }

        @Test
        @DisplayName("Should get user by id with success when not found")
        void shouldGetUserByIdWithSuccessWhenNotFound() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            when(userService.findById(userId)).thenThrow(new UserNotFoundException(userId));

            // Act
            Exception exception = assertThrows(UserNotFoundException.class, () -> userController.findByIdUser(userId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo(new UserNotFoundException(userId).getMessage());
            verify(userService).findById(userId);
        }
    }

    @Nested
    class updateUser {
        @Test
        @DisplayName("Should update user success")
        void shouldUpdateUserSuccess() {
            // Arrange
            String id = UUID.randomUUID().toString();
            UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO(
                    "Name",
                    "LastName",
                    "123456789",
                    "Bio",
                    "test@example.com",
                    "Password123+",
                    "Password123+"
            );
            ResponseEntity<TokenResponseDTO> expectedResponse =
                    ResponseEntity.ok().body(new TokenResponseDTO(
                            "Token", "RefreshToken"));
            when(userService.processUpdateUser(id, updateUserRequestDTO, authentication)).thenReturn(expectedResponse);

            // Act
            ResponseEntity<TokenResponseDTO> responseEntity = userController.updateUser(id, updateUserRequestDTO, authentication);

            // Assert
            assertThat(responseEntity)
                    .isEqualTo(expectedResponse)
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.OK);
            verify(userService).processUpdateUser(id, updateUserRequestDTO, authentication);
        }

        @Test
        @DisplayName("Should return not found when user is not found")
        void shouldReturnNotFoundWhenUserNotFound() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            UpdateUserRequestDTO updateUserRequestDTO = new UpdateUserRequestDTO(
                    "Name",
                    "LastName",
                    "123456789",
                    "Bio",
                    "test@example.com",
                    "Password123+",
                    "Password123+"
            );
            when(userService.processUpdateUser(userId, updateUserRequestDTO, authentication))
                    .thenThrow(new UserNotFoundException(userId));

            // Act
            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> userController.updateUser(userId, updateUserRequestDTO, authentication));

            // Assert
            assertThat(exception.getMessage()).isEqualTo(new UserNotFoundException(userId).getMessage());
            verify(userService).processUpdateUser(userId, updateUserRequestDTO, authentication);
        }
    }

    @Nested
    class deleteUser {
        @Test
        @DisplayName("Should delete user success")
        void shouldDeleteUserSuccess() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();
            when(userService.processDeleteUser(userId)).thenReturn(expectedResponse);

            // Act
            ResponseEntity<Void> responseEntity = userController.deleteUser(userId);

            // Assert
            assertThat(responseEntity)
                    .isEqualTo(expectedResponse)
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
            verify(userService).processDeleteUser(userId);
        }

        @Test
        @DisplayName("Should return not found when user is not found")
        void shouldReturnNotFoundWhenUserNotFound() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            when(userService.processDeleteUser(userId))
                    .thenThrow(new UserNotFoundException(userId));

            // Act
            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> userController.deleteUser(userId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo(new UserNotFoundException(userId).getMessage());
            verify(userService).processDeleteUser(userId);
        }
    }

    @Nested
    class findPostsByUserId {
        @Test
        @DisplayName("Should find posts by user id")
        void shouldFindPostsByUserId() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            List<PostResponseDTO> expectedResponse = new ArrayList<>();
            ResponseEntity<List<PostResponseDTO>> expectedResponseEntity = ResponseEntity.ok(expectedResponse);
            when(userService.findPostsByUserId(userId)).thenReturn(expectedResponseEntity );

            // Act
            ResponseEntity<List<PostResponseDTO>> responseEntity = userController.findAllPostsByUserId(userId);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).findPostsByUserId(userId);
        }

        @Test
        @DisplayName("Should return not found when user is not found")
        void shouldReturnNotFoundWhenUserNotFound() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            when(userService.findPostsByUserId(userId))
                    .thenThrow(new UserNotFoundException(userId));

            // Act
            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> userController.findAllPostsByUserId(userId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo(new UserNotFoundException(userId).getMessage());
            verify(userService).findPostsByUserId(userId);
        }
    }

    @Nested
    class changePassword {
        @Test
        @DisplayName("Should change password successfully")
        void shouldChangePasswordSuccessfully() {
            // Arrange
            ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO(
                    "Password123+",
                    "Password123+"
            );
            ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();
            when(userService.processChangePassword(changePasswordRequestDTO, authentication)).thenReturn(expectedResponse);

            // Act
            ResponseEntity<Void> responseEntity = userController.processChangePassword(changePasswordRequestDTO, authentication);

            // Assert
            assertThat(responseEntity)
                    .isEqualTo(expectedResponse)
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
            verify(userService).processChangePassword(changePasswordRequestDTO, authentication);
        }

        @Test
        @DisplayName("Should return not found when user is not found")
        void shouldReturnNotFoundWhenUserNotFound() {
            // Arrange
            String userId = UUID.randomUUID().toString();
            ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO(
                    "Password123+",
                    "Password123+"
            );
            when(userService.processChangePassword(changePasswordRequestDTO, authentication))
                    .thenThrow(new UserNotFoundException(userId));

            // Act
            Exception exception = assertThrows(UserNotFoundException.class,
                    () -> userController.processChangePassword(changePasswordRequestDTO, authentication));

            // Assert
            assertThat(exception.getMessage()).isEqualTo(new UserNotFoundException(userId).getMessage());
            verify(userService).processChangePassword(changePasswordRequestDTO, authentication);
        }
    }
}
