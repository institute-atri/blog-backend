package org.instituteatri.backendblog.service.components.postcomponents;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostComponentFindAllUsersTest {

    private PostComponentFindAllUsers postComponentFindAllUsers;

    Post post = new Post();

    @BeforeEach
    void setUp() {
        postComponentFindAllUsers = new PostComponentFindAllUsers();
    }

    @Nested
    class testUpdatePostTagsWithUpdatedEntitiesMethod {

        Tag tag1 = new Tag("Tag1", "tag1-slug");
        Tag tag2 = new Tag("Tag2", "tag2-slug");
        String tagId1 = "1";
        String tagId2 = "2";

        @Test
        @DisplayName("Should update tags correctly")
        void updatePostTagsWithUpdatedEntities_ShouldUpdateTagsCorrectly() {
            // Arrange
            tag1.setId(tagId1);

            tag2.setId(tagId2);

            post.setTags(List.of(tag1, tag2));

            Tag updatedTag1 = new Tag("UpdatedTag1", "updated-tag1-slug");
            updatedTag1.setId(tagId1);

            Tag updatedTag2 = new Tag("UpdatedTag2", "updated-tag2-slug");
            updatedTag2.setId(tagId2);

            // Act
            postComponentFindAllUsers.updatePostTagsWithUpdatedEntities(post, List.of(updatedTag1, updatedTag2));

            List<Tag> updatedTags = post.getTags();

            // Assert
            assertEquals(2, updatedTags.size());
            assertEquals("UpdatedTag1", updatedTags.get(0).getName());
            assertEquals("UpdatedTag2", updatedTags.get(1).getName());
        }

        @Test
        @DisplayName("Should handle empty updated tags")
        void updatePostTagsWithUpdatedEntities_ShouldHandleEmptyUpdatedTags() {
            // Arrange
            tag1.setId("1");

            tag2.setId("2");

            post.setTags(List.of(tag1, tag2));

            List<Tag> updatedTags = Collections.emptyList();

            // Act
            postComponentFindAllUsers.updatePostTagsWithUpdatedEntities(post, updatedTags);

            // Assert
            assertEquals(Collections.emptyList(), post.getTags());
        }
    }

    @Nested
    class testUpdatePostCategoriesWithUpdatedEntitiesMethod {

        Category category1 = new Category("Category1", "category1-slug");
        Category category2 = new Category("Category2", "category2-slug");
        String categoryId1 = "1";
        String categoryId2 = "2";

        @Test
        @DisplayName("Should update categories correctly")
        void updatePostCategoriesWithUpdatedEntities_ShouldUpdateCategoriesCorrectly() {
            // Arrange
            category1.setId(categoryId1);
            category2.setId(categoryId2);
            post.setCategories(List.of(category1, category2));

            Category updatedCategory1 = new Category("UpdatedCategory1", "updated-category1-slug");
            updatedCategory1.setId(categoryId1);

            Category updatedCategory2 = new Category("UpdatedCategory2", "updated-category2-slug");
            updatedCategory2.setId(categoryId2);

            // Act
            postComponentFindAllUsers.updatePostCategoriesWithUpdatedEntities(post, List.of(updatedCategory1, updatedCategory2));

            List<Category> updatedCategories = post.getCategories();

            // Assert
            assertEquals(2, updatedCategories.size());
            assertEquals("UpdatedCategory1", updatedCategories.get(0).getName());
            assertEquals("UpdatedCategory2", updatedCategories.get(1).getName());
        }

        @Test
        @DisplayName("Should handle empty updated categories")
        void updatePostCategoriesWithUpdatedEntities_ShouldHandleEmptyUpdatedCategories() {
            // Arrange
            category1.setId(categoryId1);
            category2.setId(categoryId2);
            post.setCategories(List.of(category1, category2));

            List<Category> updatedCategories = Collections.emptyList();

            // Act
            postComponentFindAllUsers.updatePostCategoriesWithUpdatedEntities(post, updatedCategories);

            // Assert
            assertEquals(Collections.emptyList(), post.getCategories());
        }
    }

    @Nested
    class testUpdatePostAuthorWithUpdatedUserMethod {

        User user = new User();
        String userId = "1";
        String originalName = "OriginalName";
        String originalLastName = "OriginalLastName";
        String updatedName = "UpdatedName";
        String updatedLastName = "UpdatedLastName";

        @Test
        @DisplayName("Should update author correctly")
        void updatePostAuthorWithUpdatedUser_ShouldUpdateAuthorCorrectly() {
            // Arrange
            user.setId(userId);
            user.setName(originalName);
            user.setLastName(originalLastName);

            post.setUser(user);
            post.setAuthorResponseDTO(new AuthorResponseDTO(user.getName(), user.getLastName()));

            User updatedUser = new User();
            updatedUser.setId(userId);
            updatedUser.setName(updatedName);
            updatedUser.setLastName(updatedLastName);

            // Act
            postComponentFindAllUsers.updatePostAuthorWithUpdatedUser(post, List.of(updatedUser));

            // Assert
            assertEquals(updatedName, post.getAuthorResponseDTO().name());
            assertEquals(updatedLastName, post.getAuthorResponseDTO().lastName());
            assertEquals(updatedUser, post.getUser());
        }

        @Test
        @DisplayName("Should handle empty updated users")
        void updatePostAuthorWithUpdatedUser_ShouldHandleEmptyUpdatedUsers() {
            // Arrange
            user.setId(userId);
            user.setName(originalName);
            user.setLastName(originalLastName);

            post.setUser(user);
            post.setAuthorResponseDTO(new AuthorResponseDTO(user.getName(), user.getLastName()));

            // Act
            postComponentFindAllUsers.updatePostAuthorWithUpdatedUser(post, Collections.emptyList());

            // Assert
            assertEquals(originalName, post.getAuthorResponseDTO().name());
            assertEquals(originalLastName, post.getAuthorResponseDTO().lastName());
            assertEquals(user, post.getUser());
        }
    }
}
