package org.instituteatri.backendblog.service;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomExceptionEntities;
import org.instituteatri.backendblog.infrastructure.exceptions.NotAuthenticatedException;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.instituteatri.backendblog.service.components.postcomponents.PostComponentFindAllUsers;
import org.instituteatri.backendblog.service.components.postcomponents.PostCreateComponent;
import org.instituteatri.backendblog.service.components.postcomponents.PostDeleteComponent;
import org.instituteatri.backendblog.service.components.postcomponents.PostUpdateComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostComponentFindAllUsers postFindAllUsers;

    @Mock
    private PostUpdateComponent postUpdateComponent;

    @Mock
    private PostCreateComponent postCreateComponent;

    @Mock
    private PostDeleteComponent postDeleteComponent;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostService postService;

    private PostRequestDTO postRequestDTO;
    User currentUser = new User();
    private Post existingPost;
    private final String postId = "123";

    @BeforeEach
    void setUp() {
        postRequestDTO = new PostRequestDTO();
        currentUser = new User();
        existingPost = new Post();
        existingPost.setId(postId);
    }

    @Nested
    @DisplayName("Test processFindAllPosts Method")
    class testProcessFindAllPostsMethod {
        @Test
        @DisplayName("processFindAllPosts should return all posts")
        void processFindAllPosts_ShouldReturnAllPosts() {
            // Arrange
            User userPost1 = new User();
            Post allPost1 = new Post();
            allPost1.setUser(userPost1);

            User userPost2 = new User();
            Post allPost2 = new Post();
            allPost2.setUser(userPost2);
            when(postRepository.findAll()).thenReturn(List.of(allPost1, allPost2));

            PostResponseDTO postResponse1 = new PostResponseDTO();
            PostResponseDTO postResponse2 = new PostResponseDTO();
            when(modelMapper.map(allPost1, PostResponseDTO.class)).thenReturn(postResponse1);
            when(modelMapper.map(allPost2, PostResponseDTO.class)).thenReturn(postResponse2);

            // Act
            ResponseEntity<List<PostResponseDTO>> response = postService.processFindAllPosts();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<PostResponseDTO> posts = response.getBody();
            assertNotNull(posts);
            assertEquals(2, posts.size());
        }

        @Test
        @DisplayName("processFindAllPosts should throw PostNotFoundException when no posts found")
        void processFindAllPosts_ShouldThrowPostNotFoundException_WhenNoPostsFound() {
            // Arrange
            when(postRepository.findAll()).thenReturn(List.of());

            // Act & Assert
            assertThrows(PostNotFoundException.class, () -> postService.processFindAllPosts());
        }
    }

    @Nested
    @DisplayName("Test processFindById Method")
    class testProcessFindByIdMethod {
        @Test
        @DisplayName("processFindById should return post when post exists")
        void processFindById_ShouldReturnPost_WhenPostExists() {
            // Arrange
            when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
            PostResponseDTO postResponseDTO = new PostResponseDTO();
            when(modelMapper.map(existingPost, PostResponseDTO.class)).thenReturn(postResponseDTO);

            // Act
            ResponseEntity<PostResponseDTO> responseEntity = postService.processFindById(postId);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
        }

        @Test
        @DisplayName("processFindById should throw PostNotFoundException when post does not exist")
        void processFindById_ShouldThrowPostNotFoundException_WhenPostDoesNotExist() {
            // Arrange
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(PostNotFoundException.class, () -> postService.processFindById(postId));
        }
    }

    @Nested
    @DisplayName("Test processCreatePost Method")
    class testProcessCreatePostMethod {
        @Test
        @DisplayName("processCreatePost should create a new post")
        void processCreatePost_ShouldCreateNewPost() {
            // Arrange
            when(authentication.getPrincipal()).thenReturn(currentUser);
            when(authentication.isAuthenticated()).thenReturn(true);

            PostRequestDTO createdPostRequest = new PostRequestDTO();
            createdPostRequest.setId(postId);
            when(postCreateComponent.createNewPostDTOComponent(postRequestDTO, currentUser)).thenReturn(createdPostRequest);
            URI uri = UriComponentsBuilder
                    .fromUriString("http://localhost:8080")
                    .path("/{id}")
                    .buildAndExpand(postId)
                    .toUri();

            // Act
            ResponseEntity<PostRequestDTO> responseEntity = postService.processCreatePost(postRequestDTO, authentication);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            assertEquals(uri, responseEntity.getHeaders().getLocation());
            PostRequestDTO responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
        }

        @Test
        @DisplayName("processCreatePost should throw CustomExceptionEntities on error")
        void processCreatePost_ShouldThrowCustomExceptionEntities_OnError() {
            // Arrange
            when(authentication.getPrincipal()).thenReturn(currentUser);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(postCreateComponent.createNewPostDTOComponent(any(PostRequestDTO.class), any(User.class))).thenThrow(new RuntimeException());

            // Act & Assert
            assertThrows(CustomExceptionEntities.class, () -> postService.processCreatePost(postRequestDTO, authentication));
        }
    }

    @Nested
    @DisplayName("Test processUpdatePost Method")
    class testProcessUpdatePostMethod {
        @Test
        @DisplayName("processUpdatePost should update existing post")
        void processUpdatePost_ShouldUpdateExistingPost() {
            // Arrange
            when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
            doNothing().when(postUpdateComponent).verifyUserAuthorizationForPostUpdate(existingPost, currentUser);
            doNothing().when(postUpdateComponent).updatePostPropertiesComponent(existingPost, postRequestDTO);
            when(postRepository.save(existingPost)).thenReturn(existingPost);

            // Act
            ResponseEntity<Void> responseEntity = postService.processUpdatePost(postId, postRequestDTO, currentUser);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(postRepository, times(1)).save(existingPost);
        }

        @Test
        @DisplayName("processUpdatePost should throw PostNotFoundException when post does not exist")
        void processUpdatePost_ShouldThrowPostNotFoundException_WhenPostDoesNotExist() {
            // Arrange
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(PostNotFoundException.class, () -> postService.processUpdatePost(postId, postRequestDTO, currentUser));
        }
    }

    @Nested
    @DisplayName("Test processDeletePost Method")
    class testProcessDeletePostMethod {

        @BeforeEach
        void setUp() {
            when(authentication.getPrincipal()).thenReturn(currentUser);
            when(authentication.isAuthenticated()).thenReturn(true);
        }

        @Test
        @DisplayName("processDeletePost should delete existing post")
        void processDeletePost_ShouldDeleteExistingPost() {
            // Arrange
            when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
            doNothing().when(postDeleteComponent).validatePostDeleteComponent(existingPost, currentUser);
            doNothing().when(postDeleteComponent).decrementPostCountComponent(currentUser);

            // Act
            ResponseEntity<Void> responseEntity = postService.processDeletePost(postId, authentication);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(postRepository, times(1)).deleteById(existingPost.getId());
            verify(postDeleteComponent, times(1)).decrementPostCountComponent(currentUser);
        }

        @Test
        @DisplayName("processDeletePost should throw PostNotFoundException when post does not exist")
        void processDeletePost_ShouldThrowPostNotFoundException_WhenPostDoesNotExist() {
            // Arrange
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(PostNotFoundException.class, () -> postService.processDeletePost(postId, authentication));
        }
    }

    @Nested
    @DisplayName("Test updateEntitiesInThePostList Method")
    class testUpdateEntitiesInThePostListMethod {

        private List<Post> posts;
        private List<User> updatedUsers;
        private List<Tag> updatedTags;
        private List<Category> updatedCategories;
        private Post post1;
        private Post post2;

        @BeforeEach
        void setUp() {
            // Arrange
            User user1 = new User();
            user1.setId("user1");

            User user2 = new User();
            user2.setId("user2");

            post1 = new Post();
            post1.setUser(user1);
            post1.setId("post1");

            post2 = new Post();
            post2.setUser(user2);
            post2.setId("post2");

            posts = List.of(post1, post2);
            updatedUsers = List.of(user1);
            updatedTags = List.of(new Tag("Java", "Spring"));
            updatedCategories = List.of(new Category("Spring boot", "Boot"));

            when(userRepository.findAll()).thenReturn(updatedUsers);
            when(tagRepository.findAll()).thenReturn(updatedTags);
            when(categoryRepository.findAll()).thenReturn(updatedCategories);
        }

        @Test
        @DisplayName("updateEntitiesInThePostList should update and save posts correctly")
        void updateEntitiesInThePostList_ShouldUpdateAndSavePostsCorrectly() {
            // Act
            postService.updateEntitiesInThePostList(posts);

            // Assert
            verify(postRepository, times(1)).delete(post2);
            verify(postFindAllUsers, times(1)).updatePostAuthorWithUpdatedUser(post1, updatedUsers);
            verify(postFindAllUsers, times(1)).updatePostTagsWithUpdatedEntities(post1, updatedTags);
            verify(postFindAllUsers, times(1)).updatePostCategoriesWithUpdatedEntities(post1, updatedCategories);
            verify(postRepository, times(1)).save(post1);
        }
    }

    @Nested
    @DisplayName("Test validateCurrentUser Method")
    class testValidateCurrentUserMethod {
        @Test
        @DisplayName("validateCurrentUser should throw NotAuthenticatedException when currentUser is null")
        void validateCurrentUser_ShouldThrowNotAuthenticatedException_WhenCurrentUserIsNull() {
            // Act & Assert
            assertThrows(NotAuthenticatedException.class, () -> postService.validateCurrentUser(null));
        }

        @Test
        @DisplayName("validateCurrentUser should not throw any exception when currentUser is not null")
        void validateCurrentUser_ShouldNotThrowException_WhenCurrentUserIsNotNull() {
            // Arrange
            User currentUser = new User();

            // Act & Assert
            assertDoesNotThrow(() -> postService.validateCurrentUser(currentUser));
        }
    }

    @Nested
    @DisplayName("Test getCurrentUser Method")
    class testGetCurrentUserMethod {
        @Test
        @DisplayName("getCurrentUser should throw NotAuthenticatedException when authentication is null")
        void getCurrentUser_ShouldThrowNotAuthenticatedException_WhenAuthenticationIsNull() {
            // Arrange
            authentication = null;

            // Act & Assert
            assertThrows(NotAuthenticatedException.class, () -> postService.getCurrentUser(authentication));
        }

        @Test
        @DisplayName("getCurrentUser should throw NotAuthenticatedException when authentication is not authenticated")
        void getCurrentUser_ShouldThrowNotAuthenticatedException_WhenAuthenticationIsNotAuthenticated() {
            // Arrange
            when(authentication.isAuthenticated()).thenReturn(false);

            // Act & Assert
            assertThrows(NotAuthenticatedException.class, () -> postService.getCurrentUser(authentication));
        }

        @Test
        @DisplayName("getCurrentUser should return User when authentication is authenticated")
        void getCurrentUser_ShouldReturnUser_WhenAuthenticationIsAuthenticated() {
            // Arrange
            User expectedUser = new User();
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(expectedUser);

            // Act
            User actualUser = postService.getCurrentUser(authentication);

            // Assert
            assertEquals(expectedUser, actualUser);
        }
    }
}
