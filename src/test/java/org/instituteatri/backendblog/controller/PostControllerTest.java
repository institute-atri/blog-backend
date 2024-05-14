package org.instituteatri.backendblog.controller;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomExceptionEntities;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostController postController;

    private final String postId = "123";

    User user = new User();
    PostRequestDTO expectedRequest = new PostRequestDTO(
            "123",
            "Title",
            "summary",
            "body",
            "slug",
            LocalDateTime.now(),
            null,
            new AuthorResponseDTO("name", "lastName"),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>());

    @Nested
    class getAllPosts {

        @Test
        @DisplayName("Should get all posts with success")
        void shouldGetAllPostsWithSuccess() {
            // Arrange
            List<PostResponseDTO> expectedResponse = new ArrayList<>();
            expectedResponse.add(new PostResponseDTO(
                    "123",
                    "Title",
                    "summary",
                    "body",
                    "slug",
                    LocalDateTime.now(),
                    null,
                    new AuthorResponseDTO("name", "lastName"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()));
            expectedResponse.add(new PostResponseDTO(
                    "123",
                    "Title",
                    "summary",
                    "body",
                    "slug",
                    LocalDateTime.now(),
                    null,
                    new AuthorResponseDTO("name", "lastName"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()));
            when(postService.processFindAllPosts()).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<List<PostResponseDTO>> responseEntity = postController.findAllPosts();

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(postService).processFindAllPosts();
        }

        @Test
        @DisplayName("Should get all posts with success when not found")
        void shouldGetAllPostsWithSuccessWhenNotFound() {
            // Arrange
            when(postService.processFindAllPosts()).thenThrow(new PostNotFoundException("No post found"));

            // Act
            Exception exception = assertThrows(PostNotFoundException.class, () -> postController.findAllPosts());

            // Assert
            assertThat(exception.getMessage()).isEqualTo("No post found");
            verify(postService).processFindAllPosts();
        }
    }

    @Nested
    class getPostById {

        @Test
        @DisplayName("Should get post by id with success")
        void shouldGetPostByIdWithSuccess() {
            // Arrange
            PostResponseDTO expectedResponse = new PostResponseDTO(
                    "123",
                    "Title",
                    "summary",
                    "body",
                    "slug",
                    LocalDateTime.now(),
                    null,
                    new AuthorResponseDTO("name", "lastName"),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>());
            when(postService.processFindById(postId)).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<PostResponseDTO> responseEntity = postController.findByIdPost(postId);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(postService).processFindById(postId);
        }

        @Test
        @DisplayName("Should get post by id with success when not found")
        void shouldGetPostByIdWithSuccessWhenNotFound() {
            // Arrange
            when(postService.processFindById(postId)).thenThrow(
                    new PostNotFoundException("Could not find post with id:" + postId));

            // Act
            Exception exception = assertThrows(PostNotFoundException.class,
                    () -> postController.findByIdPost(postId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find post with id:" + postId);
            verify(postService).processFindById(postId);
        }
    }

    @Nested
    class createPost {

        @Test
        @DisplayName("Should create post with success")
        void shouldCreatePostWithSuccess() {
            // Arrange
            String baseUri = "http://localhost:8080";
            URI expectedUri = UriComponentsBuilder
                    .fromUriString(baseUri)
                    .path("/{id}")
                    .buildAndExpand(postId)
                    .toUri();

            when(postService.processCreatePost(expectedRequest, authentication))
                    .thenReturn(ResponseEntity.created(expectedUri).body(expectedRequest));

            // Act
            ResponseEntity<PostRequestDTO> responseEntity = postController.createPost(expectedRequest, authentication);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedRequest);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(responseEntity.getHeaders().getLocation()).isEqualTo(expectedUri);
            verify(postService).processCreatePost(expectedRequest, authentication);
        }

        @Test
        @DisplayName("Should handle error when creating post fails")
        void shouldHandleErrorWhenCreatingPostFails() {
            // Arrange
            when(postService.processCreatePost(expectedRequest, authentication))
                    .thenThrow(new CustomExceptionEntities("Could not create post"));

            // Act
            Exception exception = assertThrows(CustomExceptionEntities.class,
                    () -> postController.createPost(expectedRequest, authentication));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not create post");
            verify(postService).processCreatePost(expectedRequest, authentication);
        }
    }

    @Nested
    class updatePost {

        @Test
        @DisplayName("Should update post with success")
        void shouldUpdatePostWithSuccess() {
            // Arrange
            when(postService.processUpdatePost(postId, expectedRequest, user))
                    .thenReturn(ResponseEntity.noContent().build());

            // Act
            ResponseEntity<Void> responseEntity = postController.updatePost(postId, expectedRequest, user);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(postService).processUpdatePost(postId, expectedRequest, user);
        }

        @Test
        @DisplayName("Should return not found when post is not found")
        void shouldReturnNotFoundWhenPostIsNotFound() {
            // Arrange
            when(postService.processUpdatePost(postId, expectedRequest, user))
                    .thenThrow(new PostNotFoundException("Could not find post with id:" + postId));

            // Act
            Exception exception = assertThrows(PostNotFoundException.class,
                    () -> postController.updatePost(postId, expectedRequest, user));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find post with id:" + postId);
            verify(postService).processUpdatePost(postId, expectedRequest, user);
        }
    }

    @Nested
    class deletePost {

        @Test
        @DisplayName("Should delete post with success")
        void shouldDeletePostWithSuccess() {
            // Arrange
            when(postService.processDeletePost(postId, authentication))
                    .thenReturn(ResponseEntity.noContent().build());

            // Act
            ResponseEntity<Void> responseEntity = postController.deletePost(postId, authentication);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(postService).processDeletePost(postId, authentication);
        }

        @Test
        @DisplayName("Should return not found when post is not found")
        void shouldReturnNotFoundWhenPostIsNotFound() {
            // Arrange
            when(postService.processDeletePost(postId, authentication))
                    .thenThrow(new PostNotFoundException("Could not find post with id:" + postId));

            // Act
            Exception exception = assertThrows(PostNotFoundException.class,
                    () -> postController.deletePost(postId, authentication));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find post with id:" + postId);
            verify(postService).processDeletePost(postId, authentication);
        }
    }
}