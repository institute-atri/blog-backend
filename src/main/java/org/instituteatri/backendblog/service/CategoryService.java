package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dtos.CategoryDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.service.helpers.helpCategory.HelperComponentUpdateCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final HelperComponentUpdateCategory helperComponentUpdateCategory;

    public List<Post> findPostsByCategoryId(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return category.getPosts();
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findById(String id) {
        Optional<Category> category = categoryRepository.findById(id);
        return category.orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public ResponseEntity<Category> processCreateCategory(CategoryDTO categoryDTO) {
        Category category = new Category(categoryDTO.name(), categoryDTO.slug());

        categoryRepository.save(category);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(category.getId())
                .toUri();
        return ResponseEntity.created(uri).body(category);
    }

    public ResponseEntity<Void> processUpdateCategory(String id, CategoryDTO updatedCategoryDTO) {
        helperComponentUpdateCategory.helperUpdatedCategory(updatedCategoryDTO);
        helperComponentUpdateCategory.helperUpdate(id, updatedCategoryDTO);

        return ResponseEntity.noContent().build();
    }

    public void deleteCategory(String id) {
        Category existingCategory = findById(id);
        categoryRepository.delete(existingCategory);
    }
}
