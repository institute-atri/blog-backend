package org.instituteatri.backendblog.service.components.postcomponents;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostDeleteComponentTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostDeleteComponent postDeleteComponent;

    Post post = new Post();
    String postId = "1";

    Tag tag = new Tag("Name", "Slug");
    String tagId = "1";

    Category category = new Category("Name", "Slug");
    String categoryId = "1";

    User currentUser = new User();

    @Nested
    @DisplayName("Tests for decrementTagPostCountComponent method")
    class testDecrementTagPostCountComponentMethod {
        @Test
        @DisplayName("Should decrease the post counter and remove the post successfully")
        void decrementTagPostCountComponent_ShouldDecrementPostCountAndRemovePostSuccessfully() {
            // Arrange
            post.setId(postId);
            tag.setId(tagId);
            tag.setPostCount(5);
            tag.getPosts().add(post);

            when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
            when(tagRepository.save(tag)).thenReturn(tag);

            // Act
            postDeleteComponent.decrementTagPostCountComponent(tagId, postId);

            // Assert
            assertEquals(4, tag.getPostCount());
            assertEquals(0, tag.getPosts().size());
            verify(tagRepository, times(1)).save(tag);
        }

        @Test
        @DisplayName("Should throw TagNotFoundException when trying to decrement the post counter")
        void decrementTagPostCountComponent_ShouldThrowTagNotFoundException() {
            // Arrange
            when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () ->
                    postDeleteComponent.decrementTagPostCountComponent(tagId, postId));
        }

    }

    @Nested
    @DisplayName("Tests for decrementCategoryPostCountComponent method")
    class testDecrementCategoryPostCountComponentMethod {
        @Test
        @DisplayName("Should decrease the post counter and remove the post successfully")
        void decrementCategoryPostCountComponent_ShouldDecrementPostCountAndRemovePostSuccessfully() {
            // Arrange
            post.setId(postId);
            category.setId(categoryId);
            category.setPostCount(5);
            category.getPosts().add(post);

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);

            // Act
            postDeleteComponent.decrementCategoryPostCountComponent(categoryId, postId);

            // Assert
            assertEquals(4, category.getPostCount());
            assertEquals(0, category.getPosts().size());
            verify(categoryRepository, times(1)).save(category);
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when trying to decrement the post counter")
        void decrementCategoryPostCountComponent_ShouldThrowCategoryNotFoundException() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () ->
                    postDeleteComponent.decrementCategoryPostCountComponent(categoryId, postId));
        }
    }

    @Nested
    @DisplayName("Tests for decrementPostCountComponent method")
    class testDecrementPostCountComponentMethod {
        @Test
        @DisplayName("Should decrement the post count of the user")
        void decrementPostCountComponent_ShouldDecrementUserPostCount() {
            // Arrange
            currentUser.setPostCount(5);

            when(userRepository.save(currentUser)).thenReturn(currentUser);

            // Act
            postDeleteComponent.decrementPostCountComponent(currentUser);

            // Assert
            assertEquals(4, currentUser.getPostCount());
            verify(userRepository, times(1)).save(currentUser);
        }
    }

    @Nested
    @DisplayName("Tests for validatePostDeleteComponent method")
    class testValidatePostDeleteComponentMethod {

        Post existingPost = new Post();
        String userId = "user1";

        @Test
        @DisplayName("Should not throw exception if user has access to delete post")
        void validatePostDeleteComponent_ShouldNotThrowException() {
            // Arrange
            currentUser.setId(userId);
            existingPost.setUser(currentUser);

            // Act & Assert
            assertDoesNotThrow(() -> postDeleteComponent.validatePostDeleteComponent(existingPost, currentUser));
        }

        @Test
        @DisplayName("Should throw UserAccessDeniedException if user does not have access to delete post")
        void validatePostDeleteComponent_ShouldThrowUserAccessDeniedException() {
            // Arrange
            currentUser.setId(userId);

            User otherUser = new User();
            otherUser.setId("user2");

            existingPost.setUser(otherUser);

            // Act & Assert
            assertThrows(UserAccessDeniedException.class, () ->
                    postDeleteComponent.validatePostDeleteComponent(existingPost, currentUser));
        }
    }
}