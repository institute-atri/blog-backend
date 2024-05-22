package org.instituteatri.backendblog.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.domain.entities.UserRole;
import org.instituteatri.backendblog.dto.response.TokenResponseDTO;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TokenRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
class UserSecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private MockMvc mockMvc;

    private final String userId = "123";

    private final String postId = "123";

    private final String email = "test@localhost.com";

    private final String password = "User24k+";


    User user = new User(
            "User",
            "Test",
            "48665456",
            "Bio",
            email,
            passwordEncoder.encode(password),
            true,
            UserRole.USER);

    Post post = new Post();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        user.setId(userId);
        userRepository.save(user);

        post.setId(postId);
        post.setUser(user);
        postRepository.save(post);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        postRepository.deleteAll();
        tokenRepository.deleteAll();
    }


    @Nested
    class testGetAllUsersEndpoint {
        @Test
        @DisplayName("Should return Forbidden for GET requests to /v1/users")
        void shouldReturnForbiddenForGetUsers() throws Exception {
            mockMvc.perform(get("/v1/users"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should allow GET requests to /v1/users for all users")
        void shouldAllowGetUsers() throws Exception {
            mockMvc.perform(get("/v1/users"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testGetPostsEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/users/posts/{id} for all users")
        void shouldAllowGetUserPostsByIdForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/users/posts/" + postId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testFindUserIdEndpoint {
        @Test
        @DisplayName("Should deny GET requests to /v1/users/find/{id} for unauthorized users")
        void shouldDenyGetUserByIdForUnauthorizedUsers() throws Exception {
            mockMvc.perform(get("/v1/users/find/" + userId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should allow GET requests to /v1/users/find/{id} for ADMIN users")
        void shouldAllowGetUserByIdForAdminUsers() throws Exception {
            mockMvc.perform(get("/v1/users/find/" + userId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testUpdateUserEndpoint {

        String updateRequestBody = """
                {
                    "name": "Updated Name",
                    "lastName": "Updated Last Name",
                    "phoneNumber": "12345678901",
                    "bio": "Updated bio",
                    "email": "%s",
                    "password": "NewPassword@123",
                    "confirmPassword": "NewPassword@123"
                }
                """.formatted(email);

        @Test
        @DisplayName("Should deny PUT requests to /v1/users/update/{id} for unauthorized users")
        void shouldDenyUpdateUserForUnauthorizedUsers() throws Exception {

            mockMvc.perform(put("/v1/users/update/" + userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow PUT requests to /v1/users/update/{id} for authenticated users")
        @WithMockUser(roles = {"ADMIN", "USER"})
        void shouldAllowUpdateUserForAuthenticatedUsers() throws Exception {

            TokenResponseDTO tokenResponseAuthUser = loginUser();
            String accessTokenAuthUser = extractAccessToken(tokenResponseAuthUser);

            mockMvc.perform(put("/v1/users/update/" + userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessTokenAuthUser)
                            .content(updateRequestBody))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testDeleteUserEndpoint {
        @Test
        @DisplayName("Should deny DELETE requests to /v1/users/delete/{id} for unauthorized users")
        void shouldDenyDeleteUserForUnauthorizedUsers() throws Exception {
            mockMvc.perform(delete("/v1/users/delete/" + userId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow DELETE requests to /v1/users/delete/{id} for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowDeleteUserForAdminUsers() throws Exception {
            mockMvc.perform(delete("/v1/users/delete/" + userId))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class TestChangePasswordEndpoint {

        String changePasswordRequestBody = """
                {
                    "oldPassword": "%s",
                    "newPassword": "NewPassword@123"
                }
                """.formatted(password);

        @Test
        @DisplayName("Should deny POST requests to /v1/users/change-password for unauthorized users")
        void shouldDenyChangePasswordForUnauthorizedUsers() throws Exception {

            mockMvc.perform(post("/v1/users/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(changePasswordRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow POST requests to /v1/users/change-password for authenticated users")
        @WithMockUser(roles = {"ADMIN", "USER"})
        void shouldAllowChangePasswordForAuthenticatedUsers() throws Exception {

            TokenResponseDTO tokenResponse = loginUser();
            String accessToken = extractAccessToken(tokenResponse);

            mockMvc.perform(post("/v1/users/change-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .content(changePasswordRequestBody))
                    .andExpect(status().isNoContent());
        }
    }

    private TokenResponseDTO loginUser() throws Exception {

        String loginRequestBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);

        String loginResponse = mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(loginResponse, TokenResponseDTO.class);
    }

    private String extractAccessToken(TokenResponseDTO tokenResponse) {
        return tokenResponse.token();
    }
}