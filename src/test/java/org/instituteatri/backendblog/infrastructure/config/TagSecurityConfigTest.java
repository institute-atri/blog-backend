package org.instituteatri.backendblog.infrastructure.config;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TagSecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;

    private final String tagId = "123";

    private final String postId = "123";

    Tag tag = new Tag("New Tag", "New Slug");
    Post post = new Post();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        tag.setId(tagId);
        tagRepository.save(tag);

        post.setId(postId);
        post.setTags(List.of(tag));
        postRepository.save(post);
    }

    @AfterEach
    void tearDown() {
        tagRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Nested
    class testGetAllTagsEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/tags for all users")
        void shouldAllowGetTagsForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/tags"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testFindTagIdEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/tags/find/{id} for all users")
        void shouldAllowGetTagByIdForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/tags/find/" + tagId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testGetPostsEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/tags/posts/{id} for all users")
        void shouldAllowGetTagPostsByIdForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/tags/posts/" + postId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testCreateTagEndpoint {

        String createRequestBody = """
                {
                    "name": "New Tag",
                    "slug": "new Slug"
                }
                """;

        @Test
        @DisplayName("Should deny POST requests to /v1/tags/create for unauthorized users")
        void shouldDenyCreateTagForUnauthorizedUsers() throws Exception {

            mockMvc.perform(post("/v1/tags/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow POST requests to /v1/tags/create for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowCreateTagForAdminUsers() throws Exception {

            mockMvc.perform(post("/v1/tags/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestBody))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    @Nested
    class testUpdateTagEndpoint {

        String updateRequestBody = """ 
                {
                  "name": "Updated",
                  "slug": "updated Slug"
                }
                """;

        @Test
        @DisplayName("Should deny PUT requests to /v1/tags/update/{id} for unauthorized users")
        void shouldDenyUpdateTagForUnauthorizedUsers() throws Exception {

            mockMvc.perform(put("/v1/tags/update/" + tagId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow PUT requests to /v1/tags/update/{id} for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowUpdateTagForAdminUsers() throws Exception {

            mockMvc.perform(put("/v1/tags/update/" + tagId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestBody))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class testDeleteTagEndpoint {
        @Test
        @DisplayName("Should deny DELETE requests to /v1/tags/delete/{id} for unauthorized users")
        void shouldDenyDeleteTagForUnauthorizedUsers() throws Exception {
            mockMvc.perform(delete("/v1/tags/delete/" + tagId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow DELETE requests to /v1/tags/delete/{id} for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowDeleteTagForAdminUsers() throws Exception {
            mockMvc.perform(delete("/v1/tags/delete/" + tagId))
                    .andExpect(status().isNoContent());
        }
    }
}