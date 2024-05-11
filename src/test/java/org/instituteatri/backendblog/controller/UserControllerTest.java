package org.instituteatri.backendblog.controller;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.instituteatri.backendblog.dto.request.ChangePasswordRequestDTO;
import org.instituteatri.backendblog.dto.request.UpdateUserRequestDTO;
import org.instituteatri.backendblog.dto.response.UserResponseDTO;
import org.instituteatri.backendblog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private Validator validator;

    private MockMvc mockMvc;

    private final String longProperty = "A1@" + "x".repeat(30);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        validator = new LocalValidatorFactoryBean();
        ((LocalValidatorFactoryBean) validator).afterPropertiesSet();
    }

    @Test
    @DisplayName("When fetching all users successfully, it should return a list of users")
    void testFindAllUsers_Success() throws Exception {

        List<UserResponseDTO> users = new ArrayList<>();
        users.add(new UserResponseDTO("1", "Alice", "Smith", "123456789", "Bio1"));
        users.add(new UserResponseDTO("2", "Bob", "Johnson", "987654321", "Bio2"));

        when(userService.processFindAllUsers()).thenReturn(ResponseEntity.ok(users));

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].lastName").value("Smith"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].bio").value("Bio1"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].lastName").value("Johnson"))
                .andExpect(jsonPath("$[1].phoneNumber").value("987654321"))
                .andExpect(jsonPath("$[1].bio").value("Bio2"));
    }

    @Test
    @DisplayName("When fetching all users successfully and the list is empty, it should return an empty list")
    void testFindAllUsers_EmptyList_Success() throws Exception {

        List<UserResponseDTO> users = new ArrayList<>();

        when(userService.processFindAllUsers()).thenReturn(ResponseEntity.ok(users));

        mockMvc.perform(get("/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(users.size()));
    }


    @Test
    @DisplayName("When updating a user with a missing name, it should return a bad request")
    void testUpdateUserWithMissingName() throws Exception {
        String updateUserJson = """
                {
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name is required.");
    }

    @Test
    @DisplayName("When updating a user with a name that is too long, it should return a bad request")
    void testUpdateUserWithNameTooLong() throws Exception {
        String updateUserJson = """
                {
                    "name": "%s",
                    "lastName": "Lorem ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """.formatted(longProperty);

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Name must be between 5 and 30 characters.");
    }

    @Test
    @DisplayName("When updating a user with a missing last name, it should return a bad request")
    void testUpdateUserWithMissingLastName() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Last name is required.");
    }

    @Test
    @DisplayName("When updating a user with a last name that is too long, it should return a bad request")
    void testUpdateUserWithLastNameTooLong() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "%s",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """.formatted(longProperty);

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Last name must be between 5 and 30 characters.");
    }

    @Test
    @DisplayName("When updating a user without a phone number, it should return a bad request")
    void testUpdateUserWithMissingPhoneNumber() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Phone number is required.");
    }

    @Test
    @DisplayName("When updating a user with a phone number exceeding the maximum length, it should return a bad request")
    void testUpdateUserWithPhoneNumberExceedingMaxLength() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "123456789012",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Phone number must be less than 11 characters.");
    }

    @Test
    @DisplayName("When updating a user with a non-digit phone number, it should return a bad request")
    void testUpdateUserWithNonDigitPhoneNumber() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "123abc456",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Phone number must contain only digits.");
    }

    @Test
    @DisplayName("When updating a user with a long bio, it should return a bad request")
    void testUpdateUserWithLongBio() throws Exception {
        String longBio = "A1!".repeat(100);
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+",
                    "bio": "%s"
                }
                """.formatted(longBio);

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Bio must be less than 100 characters.");
    }

    @Test
    @DisplayName("When updating a user with missing email, it should return a bad request")
    void testUpdateUserWithMissingEmail() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email is required.");
    }

    @Test
    @DisplayName("When updating a user with invalid email format, it should return a bad request")
    void testUpdateUserWithInvalidEmail() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "invalidemail",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid email format.");
    }

    @Test
    @DisplayName("When updating a user with a short email, it should return a bad request")
    void testUpdateUserWithShortEmail() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "a@b.c",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email must be between 10 and 30 characters.");
    }

    @Test
    @DisplayName("When updating a user with a long email, it should return a bad request")
    void testUpdateUserWithLongEmail() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "%s",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test1023+"
                }
                """.formatted(longProperty);

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email must be between 10 and 30 characters.");
    }

    @Test
    @DisplayName("When updating a user with a missing password, it should return a bad request")
    void testUpdateUserWithMissingPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password is required.");
    }

    @Test
    @DisplayName("When updating a user with an invalid password, it should return a bad request")
    void testUpdateUserWithInvalidPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "invalidPassword",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
    }

    @Test
    @DisplayName("When updating a user with a short password, it should return a bad request")
    void testUpdateUserWithShortPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test23+",
                    "confirmPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(2);
        assertThat(violations.stream().map(ConstraintViolation::getMessage))
                .containsExactlyInAnyOrder(
                        "Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                        "Password must be between 10 and 30 characters."
                );
    }

    @Test
    @DisplayName("When updating a user with a missing confirm password, it should return a bad request")
    void testUpdateUserWithMissingConfirmPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Confirm password is required.");
    }

    @Test
    @DisplayName("When updating a user with an invalid confirm password, it should return a bad request")
    void testUpdateUserWithInvalidConfirmPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "invalidPassword"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
    }

    @Test
    @DisplayName("When updating a user with a short confirm password, it should return a bad request")
    void testUpdateUserWithShortConfirmPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "@Test1023+",
                    "confirmPassword": "@Test23+"
                }
                """;

        mockMvc.perform(put("/v1/users/update/id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isBadRequest());

        UpdateUserRequestDTO updateUserRequestDTO = new ObjectMapper().readValue(updateUserJson, UpdateUserRequestDTO.class);
        Set<ConstraintViolation<UpdateUserRequestDTO>> violations = validator.validate(updateUserRequestDTO);
        assertThat(violations).hasSize(2);
        assertThat(violations.stream().map(ConstraintViolation::getMessage))
                .containsExactlyInAnyOrder(
                        "Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                        "Password must be between 10 and 30 characters."
                );
    }

    @Test
    @DisplayName("When the ID is valid, it should return the corresponding user")
    void testGetUserById_WhenValidId_ThenReturnUser() {
        String userId = "testId1";
        UserResponseDTO expectedResponse = new UserResponseDTO(userId, "Lorem Ipsum", "Lorem Ipsum", "1234567890", "Some bio");
        when(userService.findById(userId)).thenReturn(expectedResponse);

        ResponseEntity<UserResponseDTO> responseEntity = userController.findByIdUser(userId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getId()).isEqualTo(expectedResponse.getId());
        assertThat(responseEntity.getBody().getName()).isEqualTo(expectedResponse.getName());
        assertThat(responseEntity.getBody().getLastName()).isEqualTo(expectedResponse.getLastName());
        assertThat(responseEntity.getBody().getPhoneNumber()).isEqualTo(expectedResponse.getPhoneNumber());
        assertThat(responseEntity.getBody().getBio()).isEqualTo(expectedResponse.getBio());
    }

    @Test
    @DisplayName("Upon receiving a valid request, it should return an empty content response")
    void deleteUser_WhenValidRequest_ThenReturnNoContentResponse() {

        final String id = "123";
        ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

        when(userService.processDeleteUser(id)).thenReturn(expectedResponse);
        ResponseEntity<Void> responseEntity = userController.deleteUser(id);

        assertThat(responseEntity)
                .isEqualTo(expectedResponse)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("When deleting a user with a valid ID, it should return no content")
    void testDeleteUser_Success() throws Exception {
        String userId = "testId1";

        when(userService.processDeleteUser(userId)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/v1/users/delete/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When deleting a user with a valid ID, it should return no content")
    void testDeleteUser_validId_returnsNoContent() {

        String validId = "valid-id";

        ResponseEntity<Void> expectedResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(userService.processDeleteUser(validId)).thenReturn(expectedResponse);

        ResponseEntity<Void> actualResponse = userController.deleteUser(validId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("When deleting a user with a not found ID, the controller should return not found")
    void testDeleteUser_notFound_returnsNotFound() {

        String notFoundId = "not-found-id";

        ResponseEntity<Void> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(userService.processDeleteUser(notFoundId)).thenReturn(expectedResponse);

        ResponseEntity<Void> actualResponse = userController.deleteUser(notFoundId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("When deleting a user with a forbidden ID, the controller should return forbidden")
    void testDeleteUser_forbidden_returnsForbidden() {

        String forbiddenId = "forbidden-id";

        ResponseEntity<Void> expectedResponse = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        when(userService.processDeleteUser(forbiddenId)).thenReturn(expectedResponse);

        ResponseEntity<Void> actualResponse = userController.deleteUser(forbiddenId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("When deleting a user with an internal server error ID, the controller should return internal server error")
    void testDeleteUser_internalServerError_returnsInternalServerError() {

        String internalServerErrorId = "internal-server-error-id";

        ResponseEntity<Void> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(userService.processDeleteUser(internalServerErrorId)).thenReturn(expectedResponse);

        ResponseEntity<Void> actualResponse = userController.deleteUser(internalServerErrorId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("When changing password with missing password, it should return a bad request")
    void testChangePasswordWithMissingPassword() throws Exception {
        String changePasswordJson = """
                {
                    "newPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(post("/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson))
                .andExpect(status().isBadRequest());

        ChangePasswordRequestDTO changePasswordRequestDTO = new ObjectMapper().readValue(changePasswordJson, ChangePasswordRequestDTO.class);
        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(changePasswordRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Old password is required.");
    }

    @Test
    @DisplayName("When changing password with missing password, it should return a bad request")
    void testChangePasswordWithMissingNewPassword() throws Exception {
        String changePasswordJson = """
                {
                    "oldPassword": "@Test1023+"
                }
                """;

        mockMvc.perform(post("/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson))
                .andExpect(status().isBadRequest());

        ChangePasswordRequestDTO changePasswordRequestDTO = new ObjectMapper().readValue(changePasswordJson, ChangePasswordRequestDTO.class);
        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(changePasswordRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Confirm password is required.");
    }

    @Test
    @DisplayName("When changing password with invalid password, it should return a bad request")
    void testChangePasswordWithInvalidPassword() throws Exception {
        String changePasswordJson = """
                {
                    "oldPassword": "@Test1023+",
                    "newPassword": "invalidPassword"
                }
                """;

        mockMvc.perform(post("/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson))
                .andExpect(status().isBadRequest());

        ChangePasswordRequestDTO changePasswordRequestDTO = new ObjectMapper().readValue(changePasswordJson, ChangePasswordRequestDTO.class);
        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(changePasswordRequestDTO);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");
    }

    @Test
    @DisplayName("When changing password with short password, it should return a bad request")
    void testChangePasswordWithShortPassword() throws Exception {
        String changePasswordJson = """
                {
                    "oldPassword": "@Test1023+",
                    "newPassword": "test11"
                }
                """;

        mockMvc.perform(post("/v1/users/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson))
                .andExpect(status().isBadRequest());

        ChangePasswordRequestDTO changePasswordRequestDTO = new ObjectMapper().readValue(changePasswordJson, ChangePasswordRequestDTO.class);
        Set<ConstraintViolation<ChangePasswordRequestDTO>> violations = validator.validate(changePasswordRequestDTO);
        assertThat(violations).hasSize(2);
        assertThat(violations.stream().map(ConstraintViolation::getMessage))
                .containsExactlyInAnyOrder(
                        "Password must be strong and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.",
                        "Password must be between 10 and 30 characters."
                );
    }
}