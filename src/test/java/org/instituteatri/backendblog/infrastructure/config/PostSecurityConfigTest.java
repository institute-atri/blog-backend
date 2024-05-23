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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PostSecurityConfigTest {

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
            UserRole.ADMIN);

    Post post = new Post();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

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
    class TestGetAllPostsEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/posts for all users")
        void shouldAllowGetPostsForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/posts"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class TestFindPostByIdEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/posts/find/{id} for all users")
        void shouldAllowGetPostByIdForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/posts/find/" + postId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class TestCreatePostEndpoint {

        LocalDateTime createdAt = LocalDateTime.now();

        String createRequestBody = """
                {
                    "title": "New Title",
                    "summary": "New summary",
                    "body": "New body",
                    "slug": "new-slug",
                    "createdAt": "%s",
                    "authorResponseDTO": {"name": "%s", "lastName": "%s"},
                    "categories": [],
                    "tags": [],
                    "comments": []
                }
                """.formatted(createdAt, user.getName(), user.getLastName());

        @Test
        @DisplayName("Should deny POST requests to /v1/posts/create for unauthorized users")
        void shouldDenyCreatePostForUnauthorizedUsers() throws Exception {
            mockMvc.perform(post("/v1/posts/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow POST requests to /v1/posts/create for ADMIN users")
        void shouldAllowCreatePostForAdminUsers() throws Exception {

            TokenResponseDTO tokenResponse = loginUser();
            String accessToken = extractAccessToken(tokenResponse);

            mockMvc.perform(post("/v1/posts/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .content(createRequestBody))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    class TestUpdatePostEndpoint {

        String updateRequestBody = """
                {
                    "title": "Updated Title",
                    "summary": "Updated summary",
                    "body": "Updated body",
                    "slug": "updated-slug"
                }
                """;

        @Test
        @DisplayName("Should deny PUT requests to /v1/posts/update/{id} for unauthorized users")
        void shouldDenyUpdatePostForUnauthorizedUsers() throws Exception {
            mockMvc.perform(put("/v1/posts/update/" + postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow PUT requests to /v1/posts/update/{id} for authenticated users")
        void shouldAllowUpdatePostForAuthenticatedUsers() throws Exception {

            TokenResponseDTO tokenResponse = loginUser();
            String accessToken = extractAccessToken(tokenResponse);

            mockMvc.perform(put("/v1/posts/update/" + postId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + accessToken)
                            .content(updateRequestBody))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class TestDeletePostEndpoint {
        @Test
        @DisplayName("Should deny DELETE requests to /v1/posts/delete/{id} for unauthorized users")
        void shouldDenyDeletePostForUnauthorizedUsers() throws Exception {
            mockMvc.perform(delete("/v1/posts/delete/" + postId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow DELETE requests to /v1/posts/delete/{id} for authenticated users")
        void shouldAllowDeletePostForAuthenticatedUsers() throws Exception {

            TokenResponseDTO tokenResponse = loginUser();
            String accessToken = extractAccessToken(tokenResponse);

            mockMvc.perform(delete("/v1/posts/delete/" + postId)
                            .header("Authorization", "Bearer " + accessToken))
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