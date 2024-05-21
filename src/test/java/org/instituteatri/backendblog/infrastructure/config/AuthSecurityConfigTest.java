package org.instituteatri.backendblog.infrastructure.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthSecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private String refreshToken;

    private final String email = "test@localhost.com";


    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        registerUser();
        loginUser();
        refreshToken = extractRefreshToken(loginUser());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return Unauthorized status for login with invalid credentials")
    void shouldReturnUnauthorizedForInvalidLogin() throws Exception {

        String requestBodyInvalidCredentials = """
                {
                    "email": "%s",
                    "password": "WrongPassword123"
                }
                """.formatted(email);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyInvalidCredentials))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return BadRequest status for registration with duplicate email")
    void shouldReturnBadRequestForDuplicateRegistration() throws Exception {

        String requestBodyDuplicateEmail = """
                {
                    "name": "Duplicate Test",
                    "lastName": "User",
                    "phoneNumber": "1234567890",
                    "bio": "Some bio text",
                    "email":  "%s",
                    "password": "AnotherPassword1!",
                    "confirmPassword": "AnotherPassword1!"
                }
                """.formatted(email);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyDuplicateEmail))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should access refresh token endpoint for authenticated user")
    void shouldAccessRefreshTokenEndpointForAuthenticatedUser() throws Exception {

        String refreshTokenRequestBody = String.format("""
                {
                    "refreshToken": "%s"
                }
                """, refreshToken);

        mockMvc.perform(post("/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshTokenRequestBody))
                .andExpect(status().isOk());
    }

    private String extractRefreshToken(String loginResponse) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        return jsonNode.get("refreshToken").asText();
    }

    private void registerUser() throws Exception {

        String registerRequestBody = """
                {
                    "name": "Test name",
                    "lastName": "Test last name",
                    "phoneNumber": "1234567890",
                    "bio": "Some bio text",
                    "email": "%s",
                    "password": "StrongPassword1!",
                    "confirmPassword": "StrongPassword1!"
                }
                """.formatted(email);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestBody))
                .andExpect(status().isCreated());
    }

    private String loginUser() throws Exception {

        String loginRequestBody = """
                {
                    "email": "%s",
                    "password": "StrongPassword1!"
                }
                """.formatted(email);

        return mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }
}