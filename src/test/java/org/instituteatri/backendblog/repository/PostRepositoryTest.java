package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("Should find posts by ID successfully")
    void shouldTestFindPostsByIdSuccess() {
        // Arrange
        String postId = "123";
        User user = new User();
        user.setId("user123");
        Post expectedPost = new Post(
                "Title",
                "summary",
                "body",
                "slug",
                LocalDateTime.now(),
                user);
        expectedPost.setId(postId);
        postRepository.save(expectedPost);

        // Act
        List<Post> actualPosts = postRepository.findPostsById(postId);

        // Then
        assertThat(actualPosts).isNotEmpty();
        assertThat(actualPosts.getFirst().getId()).isEqualTo(postId);

        postRepository.delete(expectedPost);
    }

    @Test
    @DisplayName("Should not find posts by non-existent ID")
    void shouldNotFindPostsByNonExistentId() {
        // When
        List<Post> actualPosts = postRepository.findPostsById("nonExistentId");

        // Then
        assertThat(actualPosts).isEmpty();
    }
}