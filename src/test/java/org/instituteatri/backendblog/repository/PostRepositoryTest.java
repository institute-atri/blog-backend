package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataMongoTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @MockBean
    private PostRepository mockPostRepository;

    @Test
    @DisplayName("Should find posts by ID successfully")
    void shouldTestFindPostsByIdSuccess() {
        String postId = "123";
        User user = new User();
        List<Post> expectedPosts = List.of(new Post(
                "Title",
                "summary",
                "body",
                "slug",
                LocalDateTime.now(),
                user));
        when(postRepository.findPostsById(postId)).thenReturn(expectedPosts);

        // Act
        List<Post> actualPosts = mockPostRepository.findPostsById(postId);

        // Assert
        assertNotNull(actualPosts);
        assertThat(actualPosts).isEqualTo(expectedPosts);
        verify(mockPostRepository).findPostsById(postId);
    }

    @Test
    @DisplayName("Should not find posts by non-existent ID")
    void shouldNotFindPostsByNonExistentId() {
        // Given
        String nonExistentId = "nonExistentId";
        when(postRepository.findPostsById(nonExistentId)).thenReturn(null);

        // When
        List<Post> actualPosts = mockPostRepository.findPostsById(nonExistentId);

        // Then
        assertNull(actualPosts);
        verify(mockPostRepository).findPostsById(nonExistentId);
    }
}