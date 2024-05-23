package org.instituteatri.backendblog.service.components.postcomponents;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostLoadEntitiesComponentTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostLoadEntitiesComponent postLoadEntitiesComponent;

    String name1 = "Name1";
    String name2 = "Name2";
    String name3 = "Name3";
    String slug1 = "Slug1";
    String slug2 = "Slug2";
    String slug3 = "Slug3";

    @Nested
    class testCategoryPostLoadEntitiesComponent {

        Map<String, Category> loadedCategoriesMap = new HashMap<>();

        Category category1 = new Category(name1, slug1);
        String categoryId1 = "1";

        Category category2 = new Category(name2, slug2);
        String categoryId2 = "2";

        Category category3 = new Category(name3, slug3);
        String categoryId3 = "3";

        List<String> uniqueCategoryIds = Arrays.asList(categoryId1, categoryId2, categoryId3);

        List<Category> categories = new ArrayList<>();

        @Nested
        @DisplayName("Tests for loadUniqueCategoriesFromDatabase method")
        class testLoadUniqueCategoriesFromDatabaseMethod {
            @Test
            @DisplayName("Should load unique categories from database successfully")
            void loadUniqueCategoriesFromDatabase_ShouldLoadUniqueCategoriesFromDatabaseSuccessfully() {
                // Arrange
                category1.setId(categoryId1);
                category2.setId(categoryId2);
                category3.setId(categoryId3);

                List<Category> categories = Arrays.asList(category1, category2, category3, category1);

                when(categoryRepository.findAllById(uniqueCategoryIds))
                        .thenReturn(Arrays.asList(category1, category2, category3));

                // Act
                List<Category> result = postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(categories);

                // Assert
                assertEquals(3, result.size());
                assertEquals("Name1", result.get(0).getName());
                assertEquals("Name2", result.get(1).getName());
                assertEquals("Name3", result.get(2).getName());
            }

            @Test
            @DisplayName("Should handle empty category list and return empty list")
            void loadUniqueCategoriesFromDatabase_ShouldHandleEmptyCategoryListAndReturnEmptyList() {
                // Act
                List<Category> result = postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(categories);

                // Assert
                assertEquals(0, result.size());
                verify(categoryRepository, never()).findAllById(any());
            }
        }

        @Nested
        @DisplayName("Tests for getUniqueCategoryIds method")
        class testGetUniqueCategoryIdsMethod {
            @Test
            @DisplayName("Should get unique category IDs successfully")
            void getUniqueCategoryIds_ShouldGetUniqueCategoryIdsSuccessfully() {
                // Arrange
                category1.setId(categoryId1);
                category2.setId(categoryId2);
                category3.setId(categoryId3);

                categories.add(category1);
                categories.add(category2);
                categories.add(category3);

                // Act
                List<String> uniqueCategoryIds = postLoadEntitiesComponent.getUniqueCategoryIds(categories);

                // Assert
                assertEquals(3, uniqueCategoryIds.size());
                assertEquals(categoryId1, uniqueCategoryIds.get(0));
                assertEquals(categoryId2, uniqueCategoryIds.get(1));
                assertEquals(categoryId3, uniqueCategoryIds.get(2));
            }

            @Test
            @DisplayName("Should handle empty list and return empty list")
            void getUniqueCategoryIds_ShouldHandleEmptyListAndReturnEmptyList() {
                // Act
                List<String> uniqueCategoryIds = postLoadEntitiesComponent.getUniqueCategoryIds(categories);

                // Assert
                assertEquals(0, uniqueCategoryIds.size());
            }
        }

        @Nested
        @DisplayName("Tests for fetchCategoriesFromDatabase method")
        class testFetchCategoriesFromDatabaseMethod {
            @Test
            @DisplayName("Should fetch categories from database successfully")
            void fetchCategoriesFromDatabase_ShouldFetchCategoriesFromDatabaseSuccessfully() {
                // Arrange
                category1.setId(categoryId1);
                category2.setId(categoryId2);
                category3.setId(categoryId3);


                when(categoryRepository.findAllById(uniqueCategoryIds))
                        .thenReturn(Arrays.asList(category1, category2, category3));

                // Act
                Map<String, Category> fetchedCategories = postLoadEntitiesComponent.fetchCategoriesFromDatabase(uniqueCategoryIds);

                // Assert
                assertEquals(3, fetchedCategories.size());
                assertEquals(name1, fetchedCategories.get(categoryId1).getName());
                assertEquals(name2, fetchedCategories.get(categoryId2).getName());
                assertEquals(name3, fetchedCategories.get(categoryId3).getName());
            }

            @Test
            @DisplayName("Should fetch categories from database and handle empty list")
            void fetchCategoriesFromDatabase_ShouldHandleEmptyList() {
                // Arrange
                List<String> uniqueCategoryIds = List.of();

                // Act
                Map<String, Category> fetchedCategories = postLoadEntitiesComponent.fetchCategoriesFromDatabase(uniqueCategoryIds);

                // Assert
                assertEquals(0, fetchedCategories.size());
                verify(categoryRepository, never()).findAllById(any());
            }
        }

        @Nested
        @DisplayName("Tests for mapCategoryIdsToCategories method")
        class testMapCategoryIdsToCategoriesMethod {
            @Test
            @DisplayName("Should map category IDs to categories successfully")
            void mapCategoryIdsToCategories_ShouldMapCategoryIdsToCategoriesSuccessfully() {
                // Arrange
                category1.setId(categoryId1);
                category2.setId(categoryId2);
                category3.setId(categoryId3);

                loadedCategoriesMap.put(categoryId1, category1);
                loadedCategoriesMap.put(categoryId2, category2);
                loadedCategoriesMap.put(categoryId3, category3);

                // Act
                List<Category> result = postLoadEntitiesComponent.mapCategoryIdsToCategories(uniqueCategoryIds, loadedCategoriesMap);

                // Assert
                assertEquals(3, result.size());
                assertEquals(category1, result.get(0));
                assertEquals(category2, result.get(1));
                assertEquals(category3, result.get(2));
            }

            @Test
            @DisplayName("Should map category IDs to categories and filter out null values")
            void mapCategoryIdsToCategories_ShouldMapCategoryIdsToCategoriesAndFilterNullValues() {
                // Arrange
                category1.setId(categoryId1);
                category2.setId(categoryId2);
                category3.setId(categoryId3);

                loadedCategoriesMap.put(categoryId1, category1);
                loadedCategoriesMap.put(categoryId2, null);
                loadedCategoriesMap.put(categoryId3, category3);

                // Act
                List<Category> result = postLoadEntitiesComponent.mapCategoryIdsToCategories(uniqueCategoryIds, loadedCategoriesMap);

                // Assert
                assertEquals(2, result.size());
                assertEquals(category1, result.get(0));
                assertEquals(category3, result.get(1));
            }
        }
    }


    @Nested
    class testTagsPostLoadEntitiesComponent {

        Map<String, Tag> loadedTagsMap = new HashMap<>();

        Tag tag1 = new Tag(name1, slug1);
        String tagId1 = "1";

        Tag tag2 = new Tag(name2, slug2);
        String tagId2 = "2";

        Tag tag3 = new Tag(name3, slug3);
        String tagId3 = "3";

        List<String> uniqueTagIds = Arrays.asList(tagId1, tagId2, tagId3);
        List<Tag> tags = new ArrayList<>();


        @Nested
        @DisplayName("Tests for loadUniqueTagsFromDatabase method")
        class testLoadUniqueTagsFromDatabaseMethod {
            @Test
            @DisplayName("Should load unique tags from database successfully")
            void loadUniqueTagsFromDatabase_ShouldLoadUniqueTagsFromDatabaseSuccessfully() {
                // Arrange
                tag1.setId(tagId1);
                tag2.setId(tagId2);
                tag3.setId(tagId3);

                List<Tag> tags = Arrays.asList(tag1, tag2, tag3, tag1);

                when(tagRepository.findAllById(uniqueTagIds))
                        .thenReturn(Arrays.asList(tag1, tag2, tag3));

                // Act
                List<Tag> result = postLoadEntitiesComponent.loadUniqueTagsFromDatabase(tags);

                // Assert
                assertEquals(3, result.size());
                assertEquals("Name1", result.get(0).getName());
                assertEquals("Name2", result.get(1).getName());
                assertEquals("Name3", result.get(2).getName());
            }

            @Test
            @DisplayName("Should handle empty tag list and return empty list")
            void loadUniqueTagsFromDatabase_ShouldHandleEmptyTagListAndReturnEmptyList() {
                // Act
                List<Tag> result = postLoadEntitiesComponent.loadUniqueTagsFromDatabase(tags);

                // Assert
                assertEquals(0, result.size());
                verify(tagRepository, never()).findAllById(any());
            }
        }

        @Nested
        @DisplayName("Tests for getUniqueTagIds method")
        class testGetUniqueTagIdsMethod {
            @Test
            @DisplayName("Should get unique tag IDs successfully")
            void getUniqueTagIds_ShouldGetUniqueTagIdsSuccessfully() {
                // Arrange
                tag1.setId(tagId1);
                tag2.setId(tagId2);
                tag3.setId(tagId3);

                tags.add(tag1);
                tags.add(tag2);
                tags.add(tag3);

                // Act
                List<String> uniqueTagIds = postLoadEntitiesComponent.getUniqueTagIds(tags);

                // Assert
                assertEquals(3, uniqueTagIds.size());
                assertEquals(tagId1, uniqueTagIds.get(0));
                assertEquals(tagId2, uniqueTagIds.get(1));
                assertEquals(tagId3, uniqueTagIds.get(2));
            }

            @Test
            @DisplayName("Should handle empty list and return empty list")
            void getUniqueTagIds_ShouldHandleEmptyListAndReturnEmptyList() {
                // Act
                List<String> uniqueTagIds = postLoadEntitiesComponent.getUniqueTagIds(tags);

                // Assert
                assertEquals(0, uniqueTagIds.size());
            }
        }

        @Nested
        @DisplayName("Tests for fetchTagsFromDatabase method")
        class testFetchTagsFromDatabaseMethod {
            @Test
            @DisplayName("Should fetch tags from database successfully")
            void fetchTagsFromDatabase_ShouldFetchTagsFromDatabaseSuccessfully() {
                // Arrange
                tag1.setId(tagId1);
                tag2.setId(tagId2);
                tag3.setId(tagId3);

                when(tagRepository.findAllById(uniqueTagIds))
                        .thenReturn(Arrays.asList(tag1, tag2, tag3));

                // Act
                Map<String, Tag> fetchedTags = postLoadEntitiesComponent.fetchTagsFromDatabase(uniqueTagIds);

                // Assert
                assertEquals(3, fetchedTags.size());
                assertEquals(name1, fetchedTags.get(tagId1).getName());
                assertEquals(name2, fetchedTags.get(tagId2).getName());
                assertEquals(name3, fetchedTags.get(tagId3).getName());
            }

            @Test
            @DisplayName("Should fetch tags from database and handle empty list")
            void fetchTagsFromDatabase_ShouldHandleEmptyList() {
                // Arrange
                List<String> uniqueTagIds = List.of();

                // Act
                Map<String, Tag> fetchedTags = postLoadEntitiesComponent.fetchTagsFromDatabase(uniqueTagIds);

                // Assert
                assertEquals(0, fetchedTags.size());
                verify(tagRepository, never()).findAllById(any());
            }
        }

        @Nested
        @DisplayName("Tests mapTagIdsToTags method")
        class TestMapTagIdsToTagsMethod {
            @Test
            @DisplayName("Should map tag IDs to tags successfully")
            void mapTagIdsToTags_ShouldMapTagIdsToTagsSuccessfully() {
                // Arrange
                tag1.setId(tagId1);
                tag2.setId(tagId2);
                tag3.setId(tagId3);

                loadedTagsMap.put(tagId1, tag1);
                loadedTagsMap.put(tagId2, tag2);
                loadedTagsMap.put(tagId3, tag3);

                // Act
                List<Tag> mappedTags = postLoadEntitiesComponent.mapTagIdsToTags(uniqueTagIds, loadedTagsMap);

                // Assert
                assertEquals(3, mappedTags.size());
                assertEquals(tag1, mappedTags.get(0));
                assertEquals(tag2, mappedTags.get(1));
                assertEquals(tag3, mappedTags.get(2));
            }

            @Test
            @DisplayName("Should map tag IDs to tags and filter out null values")
            void mapTagIdsToTags_ShouldMapTagIdsToTagsAndFilterNullValues() {
                // Arrange
                tag1.setId(tagId1);
                tag2.setId(tagId2);
                tag3.setId(tagId3);

                loadedTagsMap.put(tagId1, tag1);
                loadedTagsMap.put(tagId2, null);
                loadedTagsMap.put(tagId3, tag3);

                // Act
                List<Tag> mappedTags = postLoadEntitiesComponent.mapTagIdsToTags(uniqueTagIds, loadedTagsMap);

                // Assert
                assertEquals(2, mappedTags.size());
                assertEquals(tag1, mappedTags.get(0));
                assertEquals(tag3, mappedTags.get(1));
            }
        }
    }
}

