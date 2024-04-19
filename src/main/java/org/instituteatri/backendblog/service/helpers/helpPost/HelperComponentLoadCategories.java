package org.instituteatri.backendblog.service.helpers.helpPost;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HelperComponentLoadCategories {


    private final CategoryRepository categoryRepository;


    public List<Category> loadCategories(List<Category> categories) {
        List<Category> loadedCategories = new ArrayList<>();
        for (Category category : categories) {
            Category loadedCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new CategoryNotFoundException(category.getId()));
            loadedCategories.add(loadedCategory);
        }
        return loadedCategories;
    }
}
