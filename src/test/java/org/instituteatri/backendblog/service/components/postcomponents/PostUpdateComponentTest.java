package org.instituteatri.backendblog.service.components.postcomponents;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostUpdateComponentTest {

    @Mock
    private PostLoadEntitiesComponent postLoadEntitiesComponent;

    @Mock
    Consumer<String> setter;

    @InjectMocks
    private PostUpdateComponent postUpdateComponent;

    List<Category> newCategories = new ArrayList<>();

    List<Tag> newTags = new ArrayList<>();

    User existingUser = new User();

    User currentUser = new User();

    Post existingPost = new Post();


    @Nested
    @DisplayName("Tests for verifyUserAuthorizationForPostUpdate method")
    class testVerifyUserAuthorizationForPostUpdateMethod {

        @Test
        @DisplayName("Should allow post update if user is authorized")
        void verifyUserAuthorizationForPostUpdate_ShouldAllowPostUpdateIfUserIsAuthorized() {
            // Arrange
            existingUser.setId("user1");

            currentUser.setId("user1");

            existingPost.setUser(existingUser);

            // Act & Assert
            postUpdateComponent.verifyUserAuthorizationForPostUpdate(existingPost, currentUser);
        }

        @Test
        @DisplayName("Should throw UserAccessDeniedException if user is not authorized")
        void verifyUserAuthorizationForPostUpdate_ShouldThrowUserAccessDeniedExceptionIfUserIsNotAuthorized() {
            // Arrange
            existingUser.setId("user1");

            currentUser.setId("user2");

            existingPost.setUser(existingUser);

            // Act & Assert
            assertThrows(UserAccessDeniedException.class, () ->
                    postUpdateComponent.verifyUserAuthorizationForPostUpdate(existingPost, currentUser));
        }
    }

    @Nested
    @DisplayName("Tests for updateTagsComponent method")
    class TestUpdateTagsComponentMethod {

        @Test
        @DisplayName("Should update tags of existing post if new tags list is not null")
        void updateTagsComponent_ShouldUpdateTagsIfNewTagsListIsNotNull() {
            // Arrange
            newTags.add(new Tag("Tag1", "slug1"));
            newTags.add(new Tag("Tag2", "slug2"));

            when(postLoadEntitiesComponent.loadUniqueTagsFromDatabase(newTags)).thenReturn(newTags);

            // Act
            postUpdateComponent.updateTagsComponent(existingPost, newTags);

            // Assert
            verify(postLoadEntitiesComponent, times(1)).loadUniqueTagsFromDatabase(newTags);
            assertEquals(newTags, existingPost.getTags());
        }

        @Test
        @DisplayName("Should set tags to null if new tags list is null")
        void updateTagsComponent_ShouldSetTagsToNullIfNewTagsListIsNull() {
            // Act
            postUpdateComponent.updateTagsComponent(existingPost, null);

            // Assert
            verify(postLoadEntitiesComponent, never()).loadUniqueTagsFromDatabase(any());
            assertNull(existingPost.getTags());
        }
    }

    @Nested
    @DisplayName("Tests for updateCategoriesComponent method")
    class TestUpdateCategoriesComponentMethod {

        @Test
        @DisplayName("Should update categories of existing post if new categories list is not null")
        void updateCategoriesComponent_ShouldUpdateCategoriesIfNewCategoriesListIsNotNull() {
            // Arrange
            newCategories.add(new Category("Category1", "slug1"));
            newCategories.add(new Category("Category2", "slug2"));

            when(postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(newCategories)).thenReturn(newCategories);

            // Act
            postUpdateComponent.updateCategoriesComponent(existingPost, newCategories);

            // Assert
            verify(postLoadEntitiesComponent, times(1)).loadUniqueCategoriesFromDatabase(newCategories);
            assertEquals(newCategories, existingPost.getCategories());
        }

        @Test
        @DisplayName("Should set categories to null if new categories list is null")
        void updateCategoriesComponent_ShouldSetCategoriesToNullIfNewCategoriesListIsNull() {
            // Act
            postUpdateComponent.updateCategoriesComponent(existingPost, null);

            // Assert
            verify(postLoadEntitiesComponent, never()).loadUniqueCategoriesFromDatabase(any());
            assertNull(existingPost.getCategories());
        }
    }

    @Nested
    @DisplayName("Tests for updateFieldComponent method")
    class TestUpdateFieldComponentMethod {

        String currentValue = "currentValue";
        String newValue = "newValue";


        @Test
        @DisplayName("Should update field if new value is not null and different from current value")
        void updateFieldComponent_ShouldUpdateFieldIfNewValueIsNotNullAndDifferentFromCurrentValue() {
            // Act
            postUpdateComponent.updateFieldComponent(setter, currentValue, newValue);

            // Assert
            verify(setter, times(1)).accept(newValue);
        }

        @Test
        @DisplayName("Should not update field if new value is null")
        void updateFieldComponent_ShouldNotUpdateFieldIfNewValueIsNull() {
            // Act
            postUpdateComponent.updateFieldComponent(setter, currentValue, null);

            // Assert
            verify(setter, never()).accept(any());
        }

        @Test
        @DisplayName("Should not update field if new value is equal to current value")
        void updateFieldComponent_ShouldNotUpdateFieldIfNewValueIsEqualToCurrentValue() {
            // Arrange
            String sameCurrentValue = "currentValue";

            // Act
            postUpdateComponent.updateFieldComponent(setter, currentValue, sameCurrentValue);

            // Assert
            verify(setter, never()).accept(any());
        }
    }

    @Nested
    @DisplayName("Tests for updatePostPropertiesComponent method")
    class TestUpdatePostPropertiesComponentMethod {

        PostRequestDTO updatedPostRequestDto = new PostRequestDTO();

        String newTitle = "New Title";
        String newSummary = "New Summary";
        String newBody = "New Body";
        String newSlug = "new-slug";

        String oldTitle = "Old Title";
        String oldSummary = "Old Summary";
        String oldBody = "Old Body";
        String oldSlug = "old-slug";


        @Test
        @DisplayName("Should update post properties when new values are provided")
        void updatePostPropertiesComponent_ShouldUpdatePostPropertiesWhenNewValuesAreProvided() {
            // Arrange
            updatedPostRequestDto.setTitle(newTitle);
            updatedPostRequestDto.setSummary(newSummary);
            updatedPostRequestDto.setBody(newBody);
            updatedPostRequestDto.setSlug(newSlug);

            newCategories.add(new Category("Category1", "slug1"));
            updatedPostRequestDto.setCategories(newCategories);

            newTags.add(new Tag("Tag1", "slug1"));
            updatedPostRequestDto.setTags(newTags);

            existingPost.setTitle(oldTitle);
            existingPost.setSummary(oldSummary);
            existingPost.setBody(oldBody);
            existingPost.setSlug(oldSlug);

            when(postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(newCategories)).thenReturn(newCategories);
            when(postLoadEntitiesComponent.loadUniqueTagsFromDatabase(newTags)).thenReturn(newTags);

            // Act
            postUpdateComponent.updatePostPropertiesComponent(existingPost, updatedPostRequestDto);

            // Assert
            assertEquals(newTitle, existingPost.getTitle());
            assertEquals(newSummary, existingPost.getSummary());
            assertEquals(newBody, existingPost.getBody());
            assertEquals(newSlug, existingPost.getSlug());
            assertEquals(newCategories, existingPost.getCategories());
            assertEquals(newTags, existingPost.getTags());
            assertNotNull(existingPost.getUpdatedAt());
        }

        @Test
        @DisplayName("Should not update properties if new values are null or equal to current values")
        void updatePostPropertiesComponent_ShouldNotUpdatePropertiesIfNewValuesAreNullOrEqual() {
            // Arrange
            updatedPostRequestDto.setTitle(null);
            updatedPostRequestDto.setSummary(oldSummary);
            updatedPostRequestDto.setBody(null);
            updatedPostRequestDto.setSlug(oldSlug);

            existingPost.setTitle(oldTitle);
            existingPost.setSummary(oldSummary);
            existingPost.setBody(oldBody);
            existingPost.setSlug(oldSlug);

            // Act
            postUpdateComponent.updatePostPropertiesComponent(existingPost, updatedPostRequestDto);

            // Assert
            assertEquals(oldTitle, existingPost.getTitle());
            assertEquals(oldSummary, existingPost.getSummary());
            assertEquals(oldBody, existingPost.getBody());
            assertEquals(oldSlug, existingPost.getSlug());
            assertNotNull(existingPost.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update categories and tags correctly")
        void updatePostPropertiesComponent_ShouldUpdateCategoriesAndTagsCorrectly() {
            // Arrange
            newCategories.add(new Category("Category1", "slug1"));

            newTags.add(new Tag("Tag1", "slug1"));

            updatedPostRequestDto.setCategories(newCategories);
            updatedPostRequestDto.setTags(newTags);


            when(postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(newCategories)).thenReturn(newCategories);
            when(postLoadEntitiesComponent.loadUniqueTagsFromDatabase(newTags)).thenReturn(newTags);

            // Act
            postUpdateComponent.updatePostPropertiesComponent(existingPost, updatedPostRequestDto);

            // Assert
            verify(postLoadEntitiesComponent, times(1)).loadUniqueCategoriesFromDatabase(newCategories);
            verify(postLoadEntitiesComponent, times(1)).loadUniqueTagsFromDatabase(newTags);
            assertEquals(newCategories, existingPost.getCategories());
            assertEquals(newTags, existingPost.getTags());
        }

        @Test
        @DisplayName("Should update updatedAt field")
        void updatePostPropertiesComponent_ShouldUpdateUpdatedAtField() {
            // Arrange
            LocalDateTime beforeUpdate = LocalDateTime.now().minusMinutes(1);
            existingPost.setUpdatedAt(beforeUpdate);

            // Act
            postUpdateComponent.updatePostPropertiesComponent(existingPost, updatedPostRequestDto);

            // Assert
            assertNotNull(existingPost.getUpdatedAt());
            assertTrue(existingPost.getUpdatedAt().isAfter(beforeUpdate));
        }
    }
}