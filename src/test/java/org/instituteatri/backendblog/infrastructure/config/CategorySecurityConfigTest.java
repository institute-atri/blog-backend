package org.instituteatri.backendblog.infrastructure.config;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
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
class CategorySecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;

    private final String categoryId = "123";

    private final String postId = "123";

    Category category = new Category("New Category", "New Slug");
    Post post = new Post();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        category.setId(categoryId);
        categoryRepository.save(category);

        post.setId(postId);
        post.setCategories(List.of(category));
        postRepository.save(post);
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Nested
    class testGetAllCategoriesEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/categories for all users")
        void shouldAllowGetCategoriesForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/categories"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testFindCategoryIdEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/categories/find/{id} for all users")
        void shouldAllowGetCategoryByIdForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/categories/find/" + categoryId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testGetPostsEndpoint {
        @Test
        @DisplayName("Should allow GET requests to /v1/categories/posts/{id} for all users")
        void shouldAllowGetCategoryPostsByIdForAllUsers() throws Exception {
            mockMvc.perform(get("/v1/categories/posts/" + postId))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class testCreateCategoryEndpoint {

        String createRequestBody = """
                    {
                        "name": "New Category",
                        "slug": "new Slug"
                    }
                    """;

        @Test
        @DisplayName("Should deny POST requests to /v1/categories/create for unauthorized users")
        void shouldDenyCreateCategoryForUnauthorizedUsers() throws Exception {
            mockMvc.perform(post("/v1/categories/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow POST requests to /v1/categories/create for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowCreateCategoryForAdminUsers() throws Exception {



            mockMvc.perform(post("/v1/categories/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createRequestBody))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
        }
    }

    @Nested
    class testUpdateCategoryEndpoint {

        String updateRequestBody = """ 
                    {
                      "name": "Updated Category",
                      "slug": "updated Slug"
                    }
                    """;

        @Test
        @DisplayName("Should deny PUT requests to /v1/categories/update/{id} for unauthorized users")
        void shouldDenyUpdateCategoryForUnauthorizedUsers() throws Exception {

            mockMvc.perform(put("/v1/categories/update/" + categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestBody))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow PUT requests to /v1/categories/update/{id} for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowUpdateCategoryForAdminUsers() throws Exception {

            mockMvc.perform(put("/v1/categories/update/" + categoryId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateRequestBody))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class testDeleteCategoryEndpoint {
        @Test
        @DisplayName("Should deny DELETE requests to /v1/categories/delete/{id} for unauthorized users")
        void shouldDenyDeleteCategoryForUnauthorizedUsers() throws Exception {
            mockMvc.perform(delete("/v1/categories/delete/" + categoryId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow DELETE requests to /v1/categories/delete/{id} for ADMIN users")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowDeleteCategoryForAdminUsers() throws Exception {
            mockMvc.perform(delete("/v1/categories/delete/" + categoryId))
                    .andExpect(status().isNoContent());
        }
    }
}