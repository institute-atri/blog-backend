package org.instituteatri.backendblog.service;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.instituteatri.backendblog.dto.request.CategoryUpdateRequestDTO;
import org.instituteatri.backendblog.dto.response.CategoryResponseDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomExceptionEntities;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category existingCategory;
    private final String categoryId = "123";

    @BeforeEach
    void setUp() {
        existingCategory = new Category("Java", "java");
        existingCategory.setId(categoryId);
    }

    @Nested
    @DisplayName("Test Update Field Method")
    class testUpdateFieldMethod {
        @Test
        @DisplayName("updateField should update the field when the new value is different")
        void updateField_ShouldUpdateField_WhenNewValueIsDifferent() {
            // Arrange
            existingCategory.setName("Spring boot");

            // Act
            categoryService.updateField(existingCategory::setName, existingCategory.getName(), "Science");

            // Assert
            assertEquals("Science", existingCategory.getName());
        }

        @Test
        @DisplayName("updateField should not update the field when the new value is null")
        void updateField_ShouldNotUpdateField_WhenNewValueIsNull() {
            // Arrange
            existingCategory.setName("Technology");

            // Act
            categoryService.updateField(existingCategory::setName, existingCategory.getName(), null);

            // Assert
            assertEquals("Technology", existingCategory.getName());
        }

        @Test
        @DisplayName("updateField should not update the field when the new value is the same as the current value")
        void updateField_ShouldNotUpdateField_WhenNewValueIsSameAsCurrentValue() {
            // Arrange
            existingCategory.setName("Books");

            // Act
            categoryService.updateField(existingCategory::setName, existingCategory.getName(), "Books");

            // Assert
            assertEquals("Books", existingCategory.getName());
        }

        @Test
        @DisplayName("updateField should update the field when the current value is null")
        void updateField_ShouldUpdateField_WhenCurrentValueIsNull() {
            // Act
            categoryService.updateField(existingCategory::setName, null, "NewTech");

            // Assert
            assertEquals("NewTech", existingCategory.getName());
        }
    }

    @Nested
    @DisplayName("Test Find All Categories Method")
    class testFindAllCategoriesMethod {
        @Test
        @DisplayName("processFindAllCategories should return all categories")
        void processFindAllCategories_ShouldReturnAllCategories() {
            // Arrange
            Category category1 = new Category("Technology", "technology");
            Category category2 = new Category("Science", "science");
            when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

            CategoryResponseDTO categoryResponse1 = new CategoryResponseDTO(category1.getId(), category1.getName(), category1.getSlug());
            CategoryResponseDTO categoryResponse2 = new CategoryResponseDTO(category2.getId(), category2.getName(), category2.getSlug());
            when(modelMapper.map(category1, CategoryResponseDTO.class)).thenReturn(categoryResponse1);
            when(modelMapper.map(category2, CategoryResponseDTO.class)).thenReturn(categoryResponse2);

            // Act
            ResponseEntity<List<CategoryResponseDTO>> response = categoryService.processFindAllCategories();

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<CategoryResponseDTO> categories = response.getBody();
            assertNotNull(categories);
            assertEquals(2, categories.size());
            assertEquals("Technology", categories.get(0).getName());
            assertEquals("Science", categories.get(1).getName());
        }

        @Test
        @DisplayName("processFindAllCategories should throw CategoryNotFoundException when no categories found")
        void processFindAllCategories_ShouldThrowCategoryNotFoundException_WhenNoCategoriesFound() {
            // Arrange
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () -> categoryService.processFindAllCategories());
        }
    }

    @Nested
    @DisplayName("Test Find Category By Id Method")
    class testFindCategoryByIdMethod {
        @Test
        @DisplayName("findById should return category when category exists")
        void findById_ShouldReturnCategory_WhenCategoryExists() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            CategoryResponseDTO categoryResponse = new CategoryResponseDTO(categoryId, "Technology", "technology");
            when(modelMapper.map(existingCategory, CategoryResponseDTO.class)).thenReturn(categoryResponse);

            // Act
            ResponseEntity<CategoryResponseDTO> responseEntity = categoryService.findById(categoryId);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            CategoryResponseDTO responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertEquals(categoryId, responseBody.getId());
            assertEquals("Technology", responseBody.getName());
        }

        @Test
        @DisplayName("findById should throw CategoryNotFoundException when category does not exist")
        void findById_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(categoryId));
        }
    }

    @Nested
    @DisplayName("Test Find Posts By Category Id Method")
    class testFindPostsByCategoryIdMethod {
        @Test
        @DisplayName("findPostsByCategoryId should return posts when category exists")
        void findPostsByCategoryId_ShouldReturnPosts_WhenCategoryExists() {
            // Arrange
            Post post1 = new Post();
            Post post2 = new Post();
            existingCategory.setPosts(List.of(post1, post2));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

            PostResponseDTO postResponse1 = new PostResponseDTO();
            PostResponseDTO postResponse2 = new PostResponseDTO();
            when(modelMapper.map(post1, PostResponseDTO.class)).thenReturn(postResponse1);
            when(modelMapper.map(post2, PostResponseDTO.class)).thenReturn(postResponse2);

            // Act
            ResponseEntity<List<PostResponseDTO>> responseEntity = categoryService.findPostsByCategoryId(categoryId);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            List<PostResponseDTO> posts = responseEntity.getBody();
            assertNotNull(posts);
            assertEquals(2, posts.size());
        }

        @Test
        @DisplayName("findPostsByCategoryId should throw CategoryNotFoundException when category does not exist")
        void findPostsByCategoryId_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () -> categoryService.findPostsByCategoryId(categoryId));
        }

        @Test
        @DisplayName("findPostsByCategoryId should return empty list when category has no posts")
        void findPostsByCategoryId_ShouldReturnEmptyList_WhenCategoryHasNoPosts() {
            // Arrange
            existingCategory.setPosts(Collections.emptyList());
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

            // Act
            ResponseEntity<List<PostResponseDTO>> response = categoryService.findPostsByCategoryId(categoryId);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            List<PostResponseDTO> posts = response.getBody();
            assertNotNull(posts);
            assertTrue(posts.isEmpty());
        }
    }

    @Nested
    @DisplayName("Test Create Category Method")
    class testCreateCategoryMethod {

        CategoryRequestDTO categoryRequest = new CategoryRequestDTO("Technology", "technology");

        @Test
        @DisplayName("processCreateCategory should create a new category")
        void processCreateCategory_ShouldCreateNewCategory() {
            // Arrange
            Category newCategory = new Category("Technology", "technology");
            newCategory.setId(categoryId);
            when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
            CategoryRequestDTO createdCategoryRequest = new CategoryRequestDTO("Technology", "technology");
            when(modelMapper.map(newCategory, CategoryRequestDTO.class)).thenReturn(createdCategoryRequest);

            URI location = UriComponentsBuilder.fromUriString("http://localhost:8080")
                    .path("/{id}")
                    .buildAndExpand(categoryId)
                    .toUri();

            // Act
            ResponseEntity<CategoryRequestDTO> responseEntity = categoryService.processCreateCategory(categoryRequest);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            assertEquals(location, responseEntity.getHeaders().getLocation());
            CategoryRequestDTO responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
        }

        @Test
        @DisplayName("processCreateCategory should throw CustomExceptionEntities on error")
        void processCreateCategory_ShouldThrowCustomExceptionEntities_OnError() {
            // Arrange
            when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException());

            // Act & Assert
            assertThrows(CustomExceptionEntities.class, () -> categoryService.processCreateCategory(categoryRequest));
        }
    }

    @Nested
    @DisplayName("Test Delete Category Method")
    class testDeleteCategoryMethod {
        @Test
        @DisplayName("processDeleteCategory should delete existing category")
        void processDeleteCategory_ShouldDeleteExistingCategory() {
            // Arrange
            Post postExistingCategory1 = new Post();
            Post postExistingCategory2 = new Post();
            List<Post> posts = List.of(postExistingCategory1, postExistingCategory2);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            when(postRepository.findPostsById(categoryId)).thenReturn(posts);

            // Act
            ResponseEntity<Void> responseEntity = categoryService.processDeleteCategory(categoryId);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(categoryRepository, times(1)).delete(existingCategory);
            verify(postRepository, times(2)).save(any(Post.class));
        }

        @Test
        @DisplayName("processDeleteCategory should throw CategoryNotFoundException when category does not exist")
        void processDeleteCategory_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () -> categoryService.processDeleteCategory(categoryId));
        }
    }

    @Nested
    @DisplayName("Test Update Category Method")
    class testUpdateCategoryMethod {

        CategoryUpdateRequestDTO categoryUpdateRequestDTO = new CategoryUpdateRequestDTO("Science", "science");

        @Test
        @DisplayName("processUpdateCategory should update existing category")
        void processUpdateCategory_ShouldUpdateExistingCategory() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
            existingCategory.setName(categoryUpdateRequestDTO.name());
            existingCategory.setSlug(categoryUpdateRequestDTO.slug());
            when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

            // Act
            ResponseEntity<Void> responseEntity = categoryService.processUpdateCategory(categoryId, categoryUpdateRequestDTO);

            // Assert
            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
            verify(categoryRepository, times(1)).save(existingCategory);
        }

        @Test
        @DisplayName("processUpdateCategory should throw CategoryNotFoundException when category does not exist")
        void processUpdateCategory_ShouldThrowCategoryNotFoundException_WhenCategoryDoesNotExist() {
            // Arrange
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(CategoryNotFoundException.class, () -> categoryService.processUpdateCategory(categoryId, categoryUpdateRequestDTO));
        }
    }
}


