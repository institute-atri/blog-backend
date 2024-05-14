package org.instituteatri.backendblog.controller;

import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.instituteatri.backendblog.dto.request.CategoryUpdateRequestDTO;
import org.instituteatri.backendblog.dto.response.CategoryResponseDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.CustomExceptionEntities;
import org.instituteatri.backendblog.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private final String categoryId = "123";
    CategoryRequestDTO expectedRequest = new CategoryRequestDTO("name", "Slug");
    CategoryResponseDTO expectedResponse = new CategoryResponseDTO("123", "name", "Slug");
    CategoryUpdateRequestDTO expectedRequestDto = new CategoryUpdateRequestDTO("name", "Slug");

    @Nested
    class getAllCategories {

        @Test
        @DisplayName("Should get all categories with success")
        void shouldGetAllCategoriesWithSuccess() {
            // Arrange
            List<CategoryResponseDTO> expectedResponse = new ArrayList<>();
            expectedResponse.add(new CategoryResponseDTO(
                    "123",
                    "name",
                    "Slug"
            ));
            expectedResponse.add(new CategoryResponseDTO(
                    "1233",
                    "name 2",
                    "Slug 2"
            ));
            when(categoryService.processFindAllCategories()).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<List<CategoryResponseDTO>> responseEntity = categoryController.findAllCategories();

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(categoryService).processFindAllCategories();
        }

        @Test
        @DisplayName("Should get all categories with success when not found")
        void shouldGetAllCategoriesWithSuccessWhenNotFound() {
            // Arrange
            when(categoryService.processFindAllCategories()).thenThrow(new CategoryNotFoundException("No categories found"));

            // Act
            Exception exception = assertThrows(CategoryNotFoundException.class, () -> categoryController.findAllCategories());

            // Assert
            assertThat(exception.getMessage()).isEqualTo("No categories found");
            verify(categoryService).processFindAllCategories();
        }
    }

    @Nested
    class getCategoryById {

        @Test
        @DisplayName("Should get category by id with success")
        void shouldGetCategoryByIdWithSuccess() {
            // Arrange
            when(categoryService.findById(categoryId)).thenReturn(ResponseEntity.ok(expectedResponse));

            // Act
            ResponseEntity<CategoryResponseDTO> responseEntity = categoryController.findCategoryById(categoryId);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(categoryService).findById(categoryId);
        }

        @Test
        @DisplayName("Should get category by id with success when not found")
        void shouldGetCategoryByIdWithSuccessWhenNotFound() {
            // Arrange
            when(categoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException("Could not find category with id:" + categoryId));

            // Act
            Exception exception = assertThrows(CategoryNotFoundException.class, () -> categoryController.findCategoryById(categoryId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find category with id:" + categoryId);
            verify(categoryService).findById(categoryId);
        }
    }

    @Nested
    class createCategory {

        @Test
        @DisplayName("Should create category with success")
        void shouldCreateCategoryWithSuccess() {
            // Arrange
            String baseUri = "http://localhost:8080";
            URI uri = UriComponentsBuilder.fromUriString(baseUri)
                    .path("/{id}")
                    .buildAndExpand(categoryId).toUri();

            when(categoryService.processCreateCategory(expectedRequest))
                    .thenReturn(ResponseEntity.created(uri).body(expectedRequest));

            // Act
            ResponseEntity<CategoryRequestDTO> responseEntity = categoryController.createCategory(expectedRequest);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedRequest);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            verify(categoryService).processCreateCategory(expectedRequest);
        }

        @Test
        @DisplayName("Should handle error when creating category fails")
        void shouldHandleErrorWhenCreatingCategoryFails() {
            // Arrange
            when(categoryService.processCreateCategory(expectedRequest)).thenThrow(new CustomExceptionEntities("Could not create category"));

            // Act
            Exception exception = assertThrows(CustomExceptionEntities.class, () -> categoryController.createCategory(expectedRequest));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not create category");
            verify(categoryService).processCreateCategory(expectedRequest);
        }
    }

    @Nested
    class updateCategory {

        @Test
        @DisplayName("Should update category with success")
        void shouldUpdateCategoryWithSuccess() {
            // Arrange
            when(categoryService.processUpdateCategory(categoryId, expectedRequestDto))
                    .thenReturn(ResponseEntity.noContent().build());

            // Act
            ResponseEntity<Void> responseEntity = categoryController.updateCategory(categoryId, expectedRequestDto);

            // Assert
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(categoryService).processUpdateCategory(categoryId, expectedRequestDto);
        }

        @Test
        @DisplayName("Should return not found when category is not found")
        void shouldReturnNotFoundWhenCategoryIsNotFound() {
            // Arrange
            when(categoryService.processUpdateCategory(categoryId, expectedRequestDto))
                    .thenThrow(new CategoryNotFoundException("Could not find category with id:" + categoryId));

            // Act
            Exception exception = assertThrows(CategoryNotFoundException.class, () -> categoryController.updateCategory(categoryId, expectedRequestDto));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find category with id:" + categoryId);
            verify(categoryService).processUpdateCategory(categoryId, expectedRequestDto);
        }

    }

    @Nested
    class deleteCategory {

        @Test
        @DisplayName("Should delete category with success")
        void shouldDeleteCategoryWithSuccess() {
            // Arrange
            ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();
            when(categoryService.processDeleteCategory(categoryId)).thenReturn(expectedResponse);

            // Act
            ResponseEntity<Void> responseEntity = categoryController.deleteCategory(categoryId);

            // Assert
            assertThat(responseEntity)
                    .isEqualTo(expectedResponse)
                    .extracting(ResponseEntity::getStatusCode)
                    .isEqualTo(HttpStatus.NO_CONTENT);
            verify(categoryService).processDeleteCategory(categoryId);
        }

        @Test
        @DisplayName("Should return not found when category is not found")
        void shouldReturnNotFoundWhenCategoryIsNotFound() {
            // Arrange
            when(categoryService.processDeleteCategory(categoryId))
                    .thenThrow(new CategoryNotFoundException("Could not find category with id:" + categoryId));

            // Act
            Exception exception = assertThrows(CategoryNotFoundException.class, () -> categoryController.deleteCategory(categoryId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find category with id:" + categoryId);
            verify(categoryService).processDeleteCategory(categoryId);
        }
    }

    @Nested
    class finPostsByCategoryId {

        @Test
        @DisplayName("Should find posts by category id")
        void shouldFindPostsByCategoryId() {
            // Arrange
            List<PostResponseDTO> expectedResponse = new ArrayList<>();
            ResponseEntity<List<PostResponseDTO>> expectedResponseEntity = ResponseEntity.ok(expectedResponse);
            when(categoryService.findPostsByCategoryId(categoryId)).thenReturn(expectedResponseEntity);

            // Act
            ResponseEntity<List<PostResponseDTO>> responseEntity = categoryController.getPostsByCategoryId(categoryId);

            // Assert
            assertThat(responseEntity.getBody()).isEqualTo(expectedResponse);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(categoryService).findPostsByCategoryId(categoryId);
        }

        @Test
        @DisplayName("Should return not found when category is not found")
        void shouldReturnNotFoundWhenCategoryIsNotFound() {
            // Arrange
            when(categoryService.findPostsByCategoryId(categoryId))
                    .thenThrow(new CategoryNotFoundException("Could not find category with id:" + categoryId));

            // Act
            Exception exception = assertThrows(CategoryNotFoundException.class, () -> categoryController.getPostsByCategoryId(categoryId));

            // Assert
            assertThat(exception.getMessage()).isEqualTo("Could not find category with id:" + categoryId);
            verify(categoryService).findPostsByCategoryId(categoryId);
        }
    }
}