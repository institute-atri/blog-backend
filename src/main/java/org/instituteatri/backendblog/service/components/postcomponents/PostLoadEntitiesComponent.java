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
     * Loads the categories from the provided list of categories and the database.
     *
     * @param categories The initial list of categories.
     * @return A list of loaded categories from the database.
     */
    public List<Category> loadCategoriesComponent(List<Category> categories) {
        List<String> uniqueCategoryIds = categories.stream()
                .map(Category::getId)
                .distinct()
                .toList();

        Map<String, Category> loadedCategoriesMap = categoryRepository.findAllById(uniqueCategoryIds)
                .stream()
                .collect(toMap(Category::getId, category -> category));


        return uniqueCategoryIds.stream()
                .map(loadedCategoriesMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Loads the tags from the provided list of tags and the database.
     *
     * @param tags The initial list of tags.
     * @return A list of loaded tags from the database.
     */
    public List<Tag> loadTagsComponent(List<Tag> tags) {
        List<String> uniqueTagIds = tags.stream()
                .map(Tag::getId)
                .distinct()
                .toList();

        Map<String, Tag> loadedTagsMap = tagRepository.findAllById(uniqueTagIds)
                .stream()
                .collect(toMap(Tag::getId, tag -> tag));

        return uniqueTagIds.stream()
                .map(loadedTagsMap::get)
                .filter(Objects::nonNull)
                .toList();
    }
}
