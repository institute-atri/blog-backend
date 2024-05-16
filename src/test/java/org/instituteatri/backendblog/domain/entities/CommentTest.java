package org.instituteatri.backendblog.domain.entities;

import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    User user = new User();
    Comment comment = new Comment();
    LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("Should set the properties correctly")
    void testProperties() {
        // Act
        user.setName("Test");
        user.setLastName("Test");
        Comment commentWithProperties = new Comment("This is a comment", now, user);

        // Assert
        assertEquals("This is a comment", commentWithProperties.getText(), "The text should be set correctly.");
        assertEquals(now, commentWithProperties.getCreatedAt(), "The createdAt should be set correctly.");
        assertNull(commentWithProperties.getUpdatedAt(), "The updatedAt should be null initially.");
        assertSame(user, commentWithProperties.getUser(), "The user should be set correctly.");
        assertNotNull(commentWithProperties.getAuthorResponseDTO(), "The authorResponseDTO should not be null.");
        assertEquals("Test", commentWithProperties.getAuthorResponseDTO().name(), "The authorResponseDTO's name should be set correctly.");
        assertEquals("Test", commentWithProperties.getAuthorResponseDTO().lastName(), "The authorResponseDTO's lastName should be set correctly.");
    }

    @Test
    @DisplayName("AllArgsConstructor should set properties correctly")
    void testAllArgsConstructor() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        AuthorResponseDTO authorResponseDTO = new AuthorResponseDTO("Test", "Test");

        // Act
        Comment commentWithAllProperties = new Comment("Test comment", createdAt, null, user, authorResponseDTO);

        // Assert
        assertEquals("Test comment", commentWithAllProperties.getText(), "The text should be set correctly.");
        assertEquals(createdAt, commentWithAllProperties.getCreatedAt(), "The createdAt should be set correctly.");
        assertNull(commentWithAllProperties.getUpdatedAt(), "The updatedAt should be null initially.");
        assertSame(user, commentWithAllProperties.getUser(), "The user should be set correctly.");
        assertSame(authorResponseDTO, commentWithAllProperties.getAuthorResponseDTO(), "The authorResponseDTO should be set correctly.");
    }

    @Test
    @DisplayName("NoArgsConstructor should initialize properties correctly")
    void testNoArgsConstructor() {
        // Assert
        assertNull(comment.getText(), "The text should be null.");
        assertNull(comment.getCreatedAt(), "The createdAt should be null.");
        assertNull(comment.getUpdatedAt(), "The updatedAt should be null.");
        assertNull(comment.getUser(), "The user should be null.");
        assertNull(comment.getAuthorResponseDTO(), "The authorResponseDTO should be null.");
    }

    @Test
    @DisplayName("Getter and Setter should work correctly")
    void testGetterSetter() {
        // Act
        user.setName("Test");
        user.setLastName("Test");
        comment.setText("Test text");
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        comment.setUser(user);

        // Assert
        assertEquals("Test text", comment.getText(), "The text should be set correctly.");
        assertEquals(now, comment.getCreatedAt(), "The createdAt should be set correctly.");
        assertEquals(now, comment.getUpdatedAt(), "The updatedAt should be set correctly.");
        assertSame(user, comment.getUser(), "The user should be set correctly.");
    }

    @Nested
    @DisplayName("Testing createdAt property")
    class SetCreatedAt {

        LocalDateTime initialCreatedAt = LocalDateTime.of(2022, 1, 1, 0, 0);
        Comment commentCreatedAt = new Comment("Text", initialCreatedAt, null);

        @Test
        @DisplayName("Should throw UnsupportedOperationException when trying to update createdAt after object creation")
        void shouldThrowExceptionWhenUpdatingCreatedAtAfterObjectCreation() {
            // Act & Assert
            assertThrows(UnsupportedOperationException.class, () ->
                            commentCreatedAt.setCreatedAt(LocalDateTime.of(2023, 1, 1, 0, 0)),
                    "Should throw UnsupportedOperationException if trying to update createdAt after object creation");
        }

        @Test
        @DisplayName("Should not update the createdAt")
        void shouldNotUpdateCreatedAt() {
            // Act & Assert
            assertThrows(UnsupportedOperationException.class, () ->
                            commentCreatedAt.setCreatedAt(LocalDateTime.of(2023, 1, 1, 0, 0)),
                    "Should throw UnsupportedOperationException if trying to update createdAt after object creation");
            assertEquals(initialCreatedAt, commentCreatedAt.getCreatedAt(), "createdAt should remain unchanged");
        }

        @Test
        @DisplayName("Should allow setting createdAt if it has not been set")
        void shouldAllowSettingCreatedAtIfNotSet() {
            // Arrange
            Comment commentWithoutCreation = new Comment();

            // Act
            LocalDateTime newCreatedAt = LocalDateTime.of(2022, 1, 1, 0, 0);
            commentWithoutCreation.setCreatedAt(newCreatedAt);

            // Assert
            assertEquals(newCreatedAt, commentWithoutCreation.getCreatedAt(), "createdAt should be set successfully");
        }
    }

    @Nested
    @DisplayName("Testing updatedAt property")
    class SetUpdatedAt {

        @Test
        @DisplayName("Should have updatedAt after createdAt when updated")
        void shouldHaveUpdatedAtAfterCreatedAtWhenUpdated() {
            // Arrange
            LocalDateTime createdAtInThePast = LocalDateTime.now().minusDays(1);
            Comment commentWithPastCreation = new Comment("Text", createdAtInThePast, null);
            LocalDateTime updateTime = LocalDateTime.now();

            // Act
            commentWithPastCreation.setUpdatedAt(updateTime);

            // Assert
            assertTrue(commentWithPastCreation.getCreatedAt().isBefore(commentWithPastCreation.getUpdatedAt()),
                    "UpdatedAt should be after createdAt");
        }

        @Test
        @DisplayName("Should not allow updatedAt to be before createdAt")
        void shouldNotAllowUpdatedAtBeforeCreatedAt() {
            // Arrange
            LocalDateTime currentCreatedAt = LocalDateTime.now();
            Comment commentWithCurrentCreation = new Comment("Text", currentCreatedAt, null);
            LocalDateTime earlierTime = currentCreatedAt.minusDays(1);

            // Act & Assert
            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    commentWithCurrentCreation.setUpdatedAt(earlierTime));

            // Assert
            String expectedMessage = "updatedAt cannot be before createdAt";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected text");
        }
    }
}