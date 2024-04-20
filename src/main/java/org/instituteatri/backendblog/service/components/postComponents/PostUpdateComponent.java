package org.instituteatri.backendblog.service.components.postComponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.instituteatri.backendblog.repository.PostRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PostUpdateComponent {

    private final PostRepository postRepository;
    private final PostLoadEntitiesComponent postLoadEntitiesComponent;

    public Post findPostByIdComponent(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public void authorizePostUpdateComponent(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new UserAccessDeniedException();
        }
    }

    public void updatePostPropertiesComponent(Post existingPost, PostDTO updatedPostDto) {
        updateFieldComponent(existingPost::setTitle, existingPost.getTitle(), updatedPostDto.title());
        updateFieldComponent(existingPost::setSummary, existingPost.getSummary(), updatedPostDto.summary());
        updateFieldComponent(existingPost::setBody, existingPost.getBody(), updatedPostDto.body());
        updateFieldComponent(existingPost::setSlug, existingPost.getSlug(), updatedPostDto.slug());

        existingPost.setUpdatedAt(LocalDateTime.now());

        updateCategoriesComponent(existingPost, updatedPostDto.categories());
        updateTagsComponent(existingPost, updatedPostDto.tags());
    }

    private <T> void updateFieldComponent(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }

    private void updateCategoriesComponent(Post existingPost, List<Category> newCategories) {
        if (newCategories != null) {
            existingPost.setCategories(postLoadEntitiesComponent.loadCategoriesComponent(newCategories));
        }
    }

    private void updateTagsComponent(Post existingPost, List<Tag> newTags) {
        if (newTags != null) {
            existingPost.setTags(postLoadEntitiesComponent.loadTagsComponent(newTags));
        }
    }
}
