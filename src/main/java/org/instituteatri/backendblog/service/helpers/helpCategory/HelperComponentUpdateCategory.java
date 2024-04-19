package org.instituteatri.backendblog.service.helpers.helpCategory;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.dtos.CategoryDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelperComponentUpdateCategory {

    private final CategoryRepository categoryRepository;

    public void helperUpdate(String id, CategoryDTO updatedCategoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        helperUpdateName(existingCategory, updatedCategoryDTO.name());
        helperUpdateSlug(existingCategory, updatedCategoryDTO.slug());

        categoryRepository.save(existingCategory);
    }

    public void helperUpdatedCategory(CategoryDTO categoryDTO) {
        new Category(categoryDTO.name(), categoryDTO.slug());
    }

    private void helperUpdateName(Category existingCategory, String newName) {
        if (newName != null && !newName.equals(existingCategory.getName())) {
            existingCategory.setName(newName);
        }
    }

    private void helperUpdateSlug(Category existingCategory, String newSlug) {
        if (newSlug != null && !newSlug.equals(existingCategory.getSlug())) {
            existingCategory.setSlug(newSlug);
        }
    }
}
