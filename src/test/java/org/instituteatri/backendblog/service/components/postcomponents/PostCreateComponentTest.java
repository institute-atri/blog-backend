package org.instituteatri.backendblog.service.components.postcomponents;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCreateComponentTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostLoadEntitiesComponent postLoadEntitiesComponent;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PostCreateComponent postCreateComponent;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    Post post = new Post();

    User user = new User();

    Category category1 = new Category("Category1", "category1-slug");
    String categoryId1 = "1";

    Category category2 = new Category("Category2", "category2-slug");
    String categoryId2 = "2";

    Tag tag1 = new Tag("Tag1", "tag1-slug");
    String tagId1 = "1";

    Tag tag2 = new Tag("Tag2", "tag2-slug");
    String tagId2 = "2";


    @Nested
    class testCreateNewPostDTOComponentMethod {

        String currentUserName = "Name";
        String currentUserLastName = "Last Name";

        @Test
        @DisplayName("Should create post request DTO")
        void createNewPostDTOComponent_ShouldCreatePostRequestDTO() {
            // Arrange
            PostRequestDTO postRequestDTO = new PostRequestDTO();
            postRequestDTO.setTitle("Title");
            postRequestDTO.setSummary("Summary");
            postRequestDTO.setBody("Body");
            postRequestDTO.setSlug("Slug");
            postRequestDTO.setCategories(List.of(category1, category2));
            postRequestDTO.setTags(List.of(tag1, tag2));

            User currentUser = new User();
            currentUser.setName(currentUserName);
            currentUser.setLastName(currentUserLastName);

            AuthorResponseDTO authorResponseDTO = new AuthorResponseDTO(currentUserName, currentUserLastName);
            postRequestDTO.setAuthorResponseDTO(authorResponseDTO);

            when(postRepository.save(any(Post.class))).thenReturn(new Post());

            when(modelMapper.map(any(Post.class), any())).thenReturn(postRequestDTO);

            // Act
            PostRequestDTO createdDTO = postCreateComponent.createNewPostDTOComponent(postRequestDTO, currentUser);

            // Assert
            assertEquals("Title", createdDTO.getTitle());
            assertEquals("Summary", createdDTO.getSummary());
            assertEquals("Body", createdDTO.getBody());
            assertEquals("Slug", createdDTO.getSlug());
            assertEquals(2, createdDTO.getCategories().size());
            assertEquals(2, createdDTO.getTags().size());
            assertEquals(currentUserName, createdDTO.getAuthorResponseDTO().name());
            assertEquals(currentUserLastName, createdDTO.getAuthorResponseDTO().lastName());

            verify(postRepository, times(1)).save(postCaptor.capture());
            assertEquals(currentUserName, postCaptor.getValue().getUser().getName());
            assertEquals(currentUserLastName, postCaptor.getValue().getUser().getLastName());
        }
    }

    @Nested
    class testIncrementTagPostCountComponentMethod {
        @Test
        @DisplayName("Should increment post count and save tag")
        void incrementTagPostCountComponent_ShouldIncrementPostCountAndSaveTag() {
            // Arrange
            Tag tag = new Tag("TagName", "tag-slug");
            tag.setId(tagId1);
            tag.setPostCount(5);

            when(tagRepository.findById(tagId1)).thenReturn(Optional.of(tag));

            // Act
            postCreateComponent.incrementTagPostCountComponent(tagId1, post);

            // Assert
            assertEquals(6, tag.getPostCount());
            assertEquals(1, tag.getPosts().size());
            assertEquals(post, tag.getPosts().getFirst());
            verify(tagRepository).save(tag);
        }

        @Test
        @DisplayName("Should throw TagNotFoundException")
        void incrementTagPostCountComponent_ShouldThrowTagNotFoundException() {
            // Arrange
            when(tagRepository.findById(tagId1)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(TagNotFoundException.class, () ->
                    postCreateComponent.incrementTagPostCountComponent(tagId1, post));
        }
    }

    @Nested
    class testCreatePostFromDTOMethod {
        @Test
        @DisplayName("Should create post from DTO correctly")
        void createPostFromDTO_ShouldCreatePostCorrectly() {
            // Arrange
            PostRequestDTO postRequestDTO = new PostRequestDTO();
            postRequestDTO.setTitle("Test Title");
            postRequestDTO.setSummary("Test Summary");
            postRequestDTO.setBody("Test Body");
            postRequestDTO.setSlug("test-slug");
            postRequestDTO.setCreatedAt(LocalDateTime.now());

            // Act
            Post newPost = postCreateComponent.createPostFromDTO(postRequestDTO);

            // Assert
            assertEquals("Test Title", newPost.getTitle());
            assertEquals("Test Summary", newPost.getSummary());
            assertEquals("Test Body", newPost.getBody());
            assertEquals("test-slug", newPost.getSlug());
            assertNotNull(newPost.getCreatedAt());
        }
    }

    @Nested
    class testSetAuthorAndUserMethod {
        @Test
        @DisplayName("Should set author and user correctly")
        void setAuthorAndUser_ShouldSetAuthorAndUserCorrectly() {
            // Arrange
            user.setName("Name");
            user.setLastName("LastName");

            // Act
            postCreateComponent.setAuthorAndUser(post, user);

            // Assert
            assertEquals("Name", post.getAuthorResponseDTO().name());
            assertEquals("LastName", post.getAuthorResponseDTO().lastName());
            assertEquals(user, post.getUser());
        }
    }

    @Nested
    class testLoadAndSetCategoriesAndTagsMethod {
        @Test
        @DisplayName("Should load and set categories and tags")
        void loadAndSetCategoriesAndTags_ShouldLoadAndSetCategoriesAndTags() {
            // Arrange
            List<Category> categories = List.of(category1, category2);

            List<Tag> tags = List.of(tag1, tag2);

            List<Category> loadedCategories = List.of(category1, category2);
            List<Tag> loadedTags = List.of(tag1, tag2);

            when(postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(anyList())).thenReturn(loadedCategories);
            when(postLoadEntitiesComponent.loadUniqueTagsFromDatabase(anyList())).thenReturn(loadedTags);

            // Act
            postCreateComponent.loadAndSetCategoriesAndTags(post, categories, tags);

            // Assert
            assertEquals(loadedCategories, post.getCategories());
            assertEquals(loadedTags, post.getTags());
            verify(postLoadEntitiesComponent, times(1)).loadUniqueCategoriesFromDatabase(categories);
            verify(postLoadEntitiesComponent, times(1)).loadUniqueTagsFromDatabase(tags);
        }
    }

    @Nested
    class testIncrementCategoryAndTagCountsMethod {
        @Test
        @DisplayName("Should increment counts for all categories and tags")
        void incrementCategoryAndTagCounts_ShouldIncrementCountsForAllCategoriesAndTags() {
            // Arrange
            category1.setId(categoryId1);

            category2.setId(categoryId2);

            tag1.setId(tagId1);

            tag2.setId(tagId2);

            when(categoryRepository.findById(categoryId1)).thenReturn(Optional.of(category1));
            when(categoryRepository.findById(categoryId2)).thenReturn(Optional.of(category2));

            when(tagRepository.findById(tagId1)).thenReturn(Optional.of(tag1));
            when(tagRepository.findById(tagId2)).thenReturn(Optional.of(tag2));

            post.setCategories(List.of(category1, category2));
            post.setTags(List.of(tag1, tag2));

            // Act
            postCreateComponent.incrementCategoryAndTagCounts(post);

            // Assert
            verify(categoryRepository, times(1)).findById(categoryId1);
            verify(categoryRepository, times(1)).findById(categoryId2);
            verify(tagRepository, times(1)).findById(tagId1);
            verify(tagRepository, times(1)).findById(tagId2);
        }
    }

    @Nested
    class testIncrementCategoryPostCountComponentMethod {
        @Test
        @DisplayName("Should increment post count and save category")
        void incrementCategoryPostCountComponent_ShouldIncrementPostCountAndSaveCategory() {
            // Arrange
            Category category = new Category("CategoryName", "category-slug");
            category.setId(categoryId1);
            category.setPostCount(5);

            when(categoryRepository.findById(categoryId1)).thenReturn(Optional.of(category));

            // Act
            postCreateComponent.incrementCategoryPostCountComponent(categoryId1, post);

            // Assert
            assertEquals(6, category.getPostCount());
            assertEquals(1, category.getPosts().size());
            assertEquals(post, category.getPosts().getFirst());
            verify(categoryRepository).save(category);
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException")
        void incrementCategoryPostCountComponent_ShouldThrowCategoryNotFoundException() {
            // Arrange
            when(categoryRepository.findById(categoryId1)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () ->
                    postCreateComponent.incrementCategoryPostCountComponent(categoryId1, post));
        }
    }

    @Nested
    class testUpdateCurrentUserMethod {
        @Test
        @DisplayName("Should add post and save user")
        void updateCurrentUser_ShouldAddPostAndSaveUser() {
            // Act
            postCreateComponent.updateCurrentUser(user, post);

            // Assert
            assertEquals(1, user.getPosts().size());
            assertEquals(post, user.getPosts().getFirst());
            verify(userRepository).save(user);
        }
    }
}