package org.instituteatri.backendblog.controller;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.instituteatri.backendblog.dto.request.UpdateUserRequestDTO;
import org.instituteatri.backendblog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void testUpdateUserWithMissingName() throws Exception {
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
    public void testUpdateUserWithNameTooLong() throws Exception {
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
    public void testUpdateUserWithMissingLastName() throws Exception {
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
    public void testUpdateUserWithLastNameTooLong() throws Exception {
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
    public void testUpdateUserWithMissingPhoneNumber() throws Exception {
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
    public void testUpdateUserWithPhoneNumberExceedingMaxLength() throws Exception {
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
    public void testUpdateUserWithNonDigitPhoneNumber() throws Exception {
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
    public void testUpdateUserWithLongBio() throws Exception {
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
    public void testUpdateUserWithInvalidEmail() throws Exception {
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
    public void testUpdateUserWithShortEmail() throws Exception {
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
    public void testUpdateUserWithLongEmail() throws Exception {
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
    public void testUpdateUserWithWeakPassword() throws Exception {
        String updateUserJson = """
                {
                    "name": "Lorem Ipsum",
                    "lastName": "Lorem Ipsum",
                    "phoneNumber": "12345678901",
                    "email": "test@example.com",
                    "password": "weakpassword",
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
    public void testUpdateUserWithMissingPassword() throws Exception {
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
    public void testUpdateUserWithInvalidPassword() throws Exception {
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
    public void testUpdateUserWithShortPassword() throws Exception {
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
    public void testUpdateUserWithMissingConfirmPassword() throws Exception {
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
    public void testUpdateUserWithInvalidConfirmPassword() throws Exception {
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
    public void testUpdateUserWithShortConfirmPassword() throws Exception {
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
}