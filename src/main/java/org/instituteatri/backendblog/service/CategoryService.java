package org.instituteatri.backendblog.service;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.instituteatri.backendblog.dto.response.CategoryResponseDTO;
import org.instituteatri.backendblog.dto.response.PostResponseDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<CategoryResponseDTO>> processFindAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDTO> responseDTOS = new ArrayList<>();

        categories.forEach(x -> responseDTOS.add(modelMapper.map(x, CategoryResponseDTO.class)));
        return ResponseEntity.ok(responseDTOS);
    }

    public CategoryResponseDTO findById(String id) {
        return categoryRepository.findById(id)
                .map(category -> modelMapper.map(category, CategoryResponseDTO.class))
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public List<PostResponseDTO> findPostsByCategoryId(String categoryId) {

        Category existingCategory = existingCategoryByIdOrThrow(categoryId);

        return existingCategory.getPosts().stream().map(post -> modelMapper.map(post, PostResponseDTO.class)).toList();
    }

    public ResponseEntity<CategoryRequestDTO> processCreateCategory(CategoryRequestDTO categoryRequestDTO) {

        Category category = new Category(categoryRequestDTO.getName(), categoryRequestDTO.getSlug());

        category = categoryRepository.save(category);

        CategoryRequestDTO createdCategoryRequestDTO = modelMapper.map(category, CategoryRequestDTO.class);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();

        return ResponseEntity.created(uri).body(createdCategoryRequestDTO);
    }

    public ResponseEntity<Void> processDeleteCategory(String categoryId) {

        Category existingCategory = existingCategoryByIdOrThrow(categoryId);

        removeCategoryFromPosts(existingCategory);

        categoryRepository.delete(existingCategory);

        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> processUpdateCategory(String categoryId, CategoryRequestDTO updatedCategoryRequestDTO) {

        Category existingCategory = existingCategoryByIdOrThrow(categoryId);

        updateCategoryProperties(existingCategory, updatedCategoryRequestDTO);

        categoryRepository.save(existingCategory);

        return ResponseEntity.noContent().build();
    }

    private void removeCategoryFromPosts(Category category) {
        List<Post> posts = postRepository.findPostsById(category.getId());
        for (Post post : posts) {
            post.getCategories().removeIf(c -> c.getId().equals(category.getId()));
            postRepository.save(post);
        }
    }

    private Category existingCategoryByIdOrThrow(String id) {
        return categoryRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
    }

    private void updateCategoryProperties(Category existingCategory, CategoryRequestDTO updatedCategoryRequestDTO) {
        updateField(existingCategory::setName, existingCategory.getName(), updatedCategoryRequestDTO.getName());
        updateField(existingCategory::setSlug, existingCategory.getSlug(), updatedCategoryRequestDTO.getSlug());
    }

    private <T> void updateField(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }
}
