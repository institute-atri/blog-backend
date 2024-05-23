package org.instituteatri.backendblog.domain.entities;

import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    User createUser = new User(
            "Name",
            "LastName",
            "123456789",
            "Bio",
            "test@example.com",
            "Password123+",
            true,
            UserRole.USER);

    LocalDateTime createdAt = LocalDateTime.now();

    Post createPost = new Post(
            "Title",
            "Summary",
            "Body",
            "Slug",
            createdAt,
            createUser);

    @Nested
    class constructorsTest {
        @Test
        @DisplayName("Should initialize properties correctly with all-args constructor")
        void shouldInitializePropertiesCorrectlyWithAllArgsConstructor() {
            // Arrange
            var updatedAt = LocalDateTime.now();
            Post testPost = getPost(updatedAt);

            // Assert
            assertEquals("1", testPost.getId(), "ID should be initialized correctly");
            assertEquals("Test Title", testPost.getTitle(), "Title should be initialized correctly");
            assertEquals("Test Summary", testPost.getSummary(), "Summary should be initialized correctly");
            assertEquals("Test Body", testPost.getBody(), "Body should be initialized correctly");
            assertEquals("test-slug", testPost.getSlug(), "Slug should be initialized correctly");
            assertEquals(createdAt, testPost.getCreatedAt(), "createdAt should be initialized correctly");
            assertEquals(updatedAt, testPost.getUpdatedAt(), "updatedAt should be initialized correctly");
            assertNotNull(testPost.getUser(), "User should not be null");
            assertTrue(testPost.getCategories().isEmpty(), "Categories should be empty");
            assertTrue(testPost.getTags().isEmpty(), "Tags should be null");
            assertNotNull(testPost.getAuthorResponseDTO(), "AuthorResponseDTO should not be null");
            assertTrue(testPost.getComments().isEmpty(), "Comments should be null");
        }

        private Post getPost(LocalDateTime updatedAt) {
            Post testPost = new Post(
                    "1",
                    "Test Title",
                    "Test Summary",
                    "Test Body",
                    "test-slug",
                    createdAt,
                    updatedAt,
                    createUser,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new AuthorResponseDTO("name", "lastName"),
                    new ArrayList<>());
            testPost.setUser(createUser);
            return testPost;
        }

        @Test
        @DisplayName("Should initialize properties correctly with no-args constructor")
        void shouldInitializePropertiesCorrectlyWithNoArgsConstructor() {
            // Arrange
            Post post = new Post();

            // Assert
            assertNull(post.getId(), "ID should be null");
            assertNull(post.getTitle(), "Title should be null");
            assertNull(post.getSummary(), "Summary should be null");
            assertNull(post.getBody(), "Body should be null");
            assertNull(post.getSlug(), "Slug should be null");
            assertNull(post.getCreatedAt(), "createdAt should be null");
            assertNull(post.getUpdatedAt(), "updatedAt should be null");
            assertNull(post.getUser(), "User should be null");
            assertTrue(post.getCategories().isEmpty(), "Categories should be empty");
            assertTrue(post.getTags().isEmpty(), "Tags should be null");
            assertNull(post.getAuthorResponseDTO(), "AuthorResponseDTO should be null");
            assertTrue(post.getComments().isEmpty(), "Comments should be null");
        }

        @Test
        @DisplayName("Should initialize properties correctly with constructor taking six parameters")
        void shouldInitializePropertiesCorrectlyWithSixParameterConstructor() {
            // Assert
            assertEquals("Title", createPost.getTitle(), "Title should be initialized correctly");
            assertEquals("Summary", createPost.getSummary(), "Summary should be initialized correctly");
            assertEquals("Body", createPost.getBody(), "Body should be initialized correctly");
            assertEquals("Slug", createPost.getSlug(), "Slug should be initialized correctly");
            assertEquals(createdAt, createPost.getCreatedAt(), "createdAt should be initialized correctly");
            assertNull(createPost.getUpdatedAt(), "updatedAt should be null");
            assertNotNull(createPost.getUser(), "User should not be null");
            assertNotNull(createPost.getAuthorResponseDTO(), "AuthorResponseDTO should not be null");
            assertTrue(createPost.getCategories().isEmpty(), "Categories should be empty");
            assertTrue(createPost.getTags().isEmpty(), "Tags should be empty");
            assertTrue(createPost.getComments().isEmpty(), "Comments should be null or empty");
        }
    }

    @Nested
    class createAuthorResponseDTO {
        @Test
        @DisplayName("Should create AuthorResponseDTO from User")
        void shouldCreateAuthorResponseDTOFromUser() {
            // Act
            AuthorResponseDTO authorResponseDTO = createPost.getAuthorResponseDTO();

            // Assert
            assertNotNull(authorResponseDTO, "AuthorResponseDTO should not be null");
            assertEquals("Name", authorResponseDTO.name(), "AuthorResponseDTO name should match user name");
            assertEquals("LastName", authorResponseDTO.lastName(), "AuthorResponseDTO last name should match user last name");
        }
    }

    @Nested
    class updateProperties {

        @Test
        @DisplayName("Should update the title correctly")
        void shouldUpdateTitleCorrectly() {
            // Arrange
            Post post = new Post("Original Title", "Summary", "Body", "Slug", LocalDateTime.now(), createUser);
            String updatedTitle = "Updated Title";

            // Act
            post.setTitle(updatedTitle);

            // Assert
            assertEquals(updatedTitle, post.getTitle(), "Title should be updated correctly");
        }

        @Test
        @DisplayName("Should not update the title if new title is same as current title")
        void shouldNotUpdateTitleIfNewTitleIsSameAsCurrentTitle() {
            // Arrange
            String originalTitle = "Original Title";
            Post post = new Post(originalTitle, "Summary", "Body", "Slug", LocalDateTime.now(), createUser);

            // Act
            post.setTitle(originalTitle);

            // Assert
            assertEquals(originalTitle, post.getTitle(), "Title should remain unchanged");
        }

        @Test
        @DisplayName("Should update the summary correctly")
        void shouldUpdateSummaryCorrectly() {
            // Arrange
            String originalSummary = "Original Summary";
            Post post = new Post("Title", originalSummary, "Body", "Slug", LocalDateTime.now(), createUser);
            String updatedSummary = "Updated Summary";

            // Act
            post.setSummary(updatedSummary);

            // Assert
            assertEquals(updatedSummary, post.getSummary(), "Summary should be updated correctly");
        }

        @Test
        @DisplayName("Should not update the summary if new summary is same as current summary")
        void shouldNotUpdateSummaryIfNewSummaryIsSameAsCurrentSummary() {
            // Arrange
            String originalSummary = "Original Summary";
            Post post = new Post("Title", originalSummary, "Body", "Slug", LocalDateTime.now(), createUser);

            // Act
            post.setSummary(originalSummary);

            // Assert
            assertEquals(originalSummary, post.getSummary(), "Summary should remain unchanged");
        }

        @Test
        @DisplayName("Should update the body correctly")
        void shouldUpdateBodyCorrectly() {
            // Arrange
            String originalBody = "Original Body";
            Post post = new Post("Title", "Summary", originalBody, "Slug", LocalDateTime.now(), createUser);
            String updatedBody = "Updated Body";

            // Act
            post.setBody(updatedBody);

            // Assert
            assertEquals(updatedBody, post.getBody(), "Body should be updated correctly");
        }

        @Test
        @DisplayName("Should not update the body if new body is same as current body")
        void shouldNotUpdateBodyIfNewBodyIsSameAsCurrentBody() {
            // Arrange
            String originalBody = "Original Body";
            Post post = new Post("Title", "Summary", originalBody, "Slug", LocalDateTime.now(), createUser);

            // Act
            post.setBody(originalBody);

            // Assert
            assertEquals(originalBody, post.getBody(), "Body should remain unchanged");
        }

        @Test
        @DisplayName("Should update the slug correctly")
        void shouldUpdateSlugCorrectly() {
            // Arrange
            String originalSlug = "original-slug";
            Post post = new Post("Title", "Summary", "Body", originalSlug, LocalDateTime.now(), createUser);
            String updatedSlug = "updated-slug";

            // Act
            post.setSlug(updatedSlug);

            // Assert
            assertEquals(updatedSlug, post.getSlug(), "Slug should be updated correctly");
        }

        @Test
        @DisplayName("Should not update the slug if new slug is same as current slug")
        void shouldNotUpdateSlugIfNewSlugIsSameAsCurrentSlug() {
            // Arrange
            String originalSlug = "original-slug";
            Post post = new Post("Title", "Summary", "Body", originalSlug, LocalDateTime.now(), createUser);

            // Act
            post.setSlug(originalSlug);

            // Assert
            assertEquals(originalSlug, post.getSlug(), "Slug should remain unchanged");
        }
    }

    @Nested
    class setCreatedAt {

        LocalDateTime originalCreatedAt = LocalDateTime.of(2022, 1, 1, 0, 0);
        Post post = new Post("Title", "Summary", "Body", "Slug", originalCreatedAt, createUser);

        @Test
        @DisplayName("Should throw UnsupportedOperationException when trying to update createdAt after object creation")
        void shouldThrowExceptionWhenUpdatingCreatedAtAfterObjectCreation() {
            // Arrange
            LocalDateTime updatedCreatedAt = LocalDateTime.of(2023, 1, 1, 0, 0); // New creation date

            // Act & Assert
            assertThrows(UnsupportedOperationException.class, () ->
                    post.setCreatedAt(updatedCreatedAt), "Should throw UnsupportedOperationException if trying to update createdAt after object creation");
        }

        @Test
        @DisplayName("Should not allow createdAt to be updated after object creation")
        void shouldNotAllowCreatedAtToBeUpdatedAfterObjectCreation() {
            // Arrange
            LocalDateTime updatedCreatedAt = LocalDateTime.of(2023, 1, 1, 0, 0); // New creation date

            // Act
            UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () ->
                            post.setCreatedAt(updatedCreatedAt),
                    "Should throw UnsupportedOperationException if trying to update createdAt after object creation");

            // Assert
            assertNotNull(exception, "Exception should not be null");
            assertEquals("createdAt cannot be updated after object creation", exception.getMessage(), "Exception message should be correct");
        }

        @Test
        @DisplayName("Should not update the createdAt")
        void shouldNotUpdateCreatedAt() {
            // Arrange
            LocalDateTime updatedCreatedAt = LocalDateTime.of(2023, 1, 1, 0, 0); // Tentativa de atualizar a data

            // Act & Assert
            assertThrows(UnsupportedOperationException.class, () -> post.setCreatedAt(updatedCreatedAt));
            assertEquals(originalCreatedAt, post.getCreatedAt(), "createdAt should remain unchanged");
        }

        @Test
        @DisplayName("Should allow setting createdAt if it has not been set")
        void shouldAllowSettingCreatedAtIfNotSet() {
            // Arrange
            Post postEmpty = new Post();
            LocalDateTime newCreatedAt = LocalDateTime.of(2022, 1, 1, 0, 0);

            // Act
            postEmpty.setCreatedAt(newCreatedAt);

            // Assert
            assertEquals(newCreatedAt, postEmpty.getCreatedAt(), "createdAt should be set successfully");
        }
    }

    @Nested
    class setUpdatedAt {

        @Test
        @DisplayName("Should have updatedAt after createdAt when updated")
        void shouldHaveUpdatedAtAfterCreatedAtWhenUpdated() {
            // Arrange
            LocalDateTime initialTime = LocalDateTime.now().minusDays(1);
            Post testInitialTimePost = new Post("Title", "Summary", "Body", "Slug", initialTime, createUser);
            LocalDateTime updateTime = LocalDateTime.now();

            // Act
            testInitialTimePost.setUpdatedAt(updateTime);

            // Assert
            assertTrue(testInitialTimePost.getCreatedAt().isBefore(testInitialTimePost.getUpdatedAt()),
                    "UpdatedAt should be after createdAt");
        }

        @Test
        @DisplayName("Should not allow updatedAt to be before createdAt")
        void shouldNotAllowUpdatedAtBeforeCreatedAt() {
            // Arrange
            LocalDateTime initialTime = LocalDateTime.now();
            Post testInitialTimePost = new Post("Title", "Summary", "Body", "Slug", initialTime, createUser);
            LocalDateTime earlierTime = initialTime.minusDays(1);

            // Act
            Exception exception = assertThrows(IllegalArgumentException.class, () ->
                    testInitialTimePost.setUpdatedAt(earlierTime));

            // Assert
            String expectedMessage = "updatedAt cannot be before createdAt";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected text");
        }
    }
}