package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.mappings.CategoryMapper;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final PostRepository postRepository;

    public ResponseEntity<List<CategoryRequestDTO>> processFindAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return ResponseEntity.ok(categories.stream()
                .map(categoryMapper::toCategoryDto)
                .toList());
    }

    public CategoryRequestDTO findById(String id) {
        Optional<Category> category = categoryRepository.findById(id);

        return category.map(categoryMapper::toCategoryDto).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<PostRequestDTO> findPostsByCategoryId(String categoryId) {
        CategoryRequestDTO categoryRequestDTO = findById(categoryId);
        return categoryRequestDTO.postRequestDTOS();
    }

    public ResponseEntity<CategoryRequestDTO> processCreateCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category(categoryRequestDTO.name(), categoryRequestDTO.slug());

        category = categoryRepository.save(category);

        CategoryRequestDTO createdCategoryRequestDTO = categoryMapper.toCategoryDto(category);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();

        return ResponseEntity.created(uri).body(createdCategoryRequestDTO);
    }

    public ResponseEntity<Void> processDeleteCategory(String id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        List<Post> posts = postRepository.findPostsById(id);
        for (Post post : posts) {
            post.getCategories().removeIf(category -> category.getId().equals(id));
            postRepository.save(post);
        }

        categoryRepository.delete(existingCategory);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> processUpdateCategory(String id, CategoryRequestDTO updatedCategoryRequestDTO) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));

        updateCategoryProperties(existingCategory, updatedCategoryRequestDTO);

        categoryRepository.save(existingCategory);

        return ResponseEntity.noContent().build();
    }

    private void updateCategoryProperties(Category existingCategory, CategoryRequestDTO updatedCategoryRequestDTO) {
        updateField(existingCategory::setName, existingCategory.getName(), updatedCategoryRequestDTO.name());
        updateField(existingCategory::setSlug, existingCategory.getSlug(), updatedCategoryRequestDTO.slug());
    }

    private <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }
}
