package org.instituteatri.backendblog.service.components.postcomponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class PostLoadEntitiesComponent {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;


    /**
     * Loads unique categories from the provided list and fetches them from the database.
     * Duplicates are removed, and only the unique categories are fetched from the database.
     *
     * @param categories The list of categories to load.
     * @return A list of unique categories fetched from the database.
     */
    public List<Category> loadUniqueCategoriesFromDatabase(List<Category> categories) {
        List<String> uniqueCategoryIds = getUniqueCategoryIds(categories);
        Map<String, Category> loadedCategoriesMap = fetchCategoriesFromDatabase(uniqueCategoryIds);
        return mapCategoryIdsToCategories(uniqueCategoryIds, loadedCategoriesMap);
    }

    protected List<String> getUniqueCategoryIds(List<Category> categories) {
        return categories.stream()
                .map(Category::getId)
                .distinct()
                .toList();
    }

    protected Map<String, Category> fetchCategoriesFromDatabase(List<String> uniqueCategoryIds) {

        if (uniqueCategoryIds.isEmpty()) {
            return Map.of();
        }

        return categoryRepository.findAllById(uniqueCategoryIds)
                .stream()
                .collect(toMap(Category::getId, category -> category));
    }

    protected List<Category> mapCategoryIdsToCategories(List<String> uniqueCategoryIds, Map<String, Category> loadedCategoriesMap) {
        return uniqueCategoryIds.stream()
                .map(loadedCategoriesMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Loads unique tags from the provided list and fetches them from the database.
     * Duplicates are removed, and only the unique tags are fetched from the database.
     *
     * @param tags The list of tags to load.
     * @return A list of unique tags fetched from the database.
     */
    public List<Tag> loadUniqueTagsFromDatabase(List<Tag> tags) {
        List<String> uniqueTagIds = getUniqueTagIds(tags);
        Map<String, Tag> loadedTagsMap = fetchTagsFromDatabase(uniqueTagIds);
        return mapTagIdsToTags(uniqueTagIds, loadedTagsMap);
    }

    protected List<String> getUniqueTagIds(List<Tag> tags) {

        return tags.stream()
                .map(Tag::getId)
                .distinct()
                .toList();
    }

    protected Map<String, Tag> fetchTagsFromDatabase(List<String> uniqueTagIds) {

        if (uniqueTagIds.isEmpty()) {
            return Map.of();
        }

        return tagRepository.findAllById(uniqueTagIds)
                .stream()
                .collect(toMap(Tag::getId, tag -> tag));
    }

    protected List<Tag> mapTagIdsToTags(List<String> uniqueTagIds, Map<String, Tag> loadedTagsMap) {

        return uniqueTagIds.stream()
                .map(loadedTagsMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
