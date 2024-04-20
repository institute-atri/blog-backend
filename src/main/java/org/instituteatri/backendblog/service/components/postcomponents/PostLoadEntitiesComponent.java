package org.instituteatri.backendblog.service.components.postComponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostLoadEntitiesComponent {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public List<Category> loadCategoriesComponent(List<Category> categories) {
        List<Category> loadedCategories = new ArrayList<>();
        for (Category category : categories) {
            Category loadedCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new CategoryNotFoundException(category.getId()));
            loadedCategories.add(loadedCategory);
        }
        return loadedCategories;
    }

    public List<Tag> loadTagsComponent(List<Tag> tags) {
        List<Tag> loadedTags = new ArrayList<>();
        for (Tag tag : tags) {
            Tag loadedTag = tagRepository.findById(tag.getId())
                    .orElseThrow(() -> new TagNotFoundException(tag.getId()));
            loadedTags.add(loadedTag);
        }
        return loadedTags;
    }
}
