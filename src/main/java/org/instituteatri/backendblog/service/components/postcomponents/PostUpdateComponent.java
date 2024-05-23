package org.instituteatri.backendblog.service.components.postcomponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PostUpdateComponent {

    private final PostLoadEntitiesComponent postLoadEntitiesComponent;

    public void verifyUserAuthorizationForPostUpdate(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new UserAccessDeniedException();
        }
    }

    public void updatePostPropertiesComponent(Post existingPost, PostRequestDTO updatedPostRequestDto) {
        updateFieldComponent(existingPost::setTitle, existingPost.getTitle(), updatedPostRequestDto.getTitle());
        updateFieldComponent(existingPost::setSummary, existingPost.getSummary(), updatedPostRequestDto.getSummary());
        updateFieldComponent(existingPost::setBody, existingPost.getBody(), updatedPostRequestDto.getBody());
        updateFieldComponent(existingPost::setSlug, existingPost.getSlug(), updatedPostRequestDto.getSlug());

        existingPost.setUpdatedAt(LocalDateTime.now());

        updateCategoriesComponent(existingPost, updatedPostRequestDto.getCategories());
        updateTagsComponent(existingPost, updatedPostRequestDto.getTags());
    }

    protected <T> void updateFieldComponent(Consumer<T> setter, T currentValue, T newValue) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue);
        }
    }

    protected void updateCategoriesComponent(Post existingPost, List<Category> newCategories) {
        existingPost.setCategories(newCategories != null ? postLoadEntitiesComponent.loadUniqueCategoriesFromDatabase(newCategories) : null);
    }

    protected void updateTagsComponent(Post existingPost, List<Tag> newTags) {
        existingPost.setTags(newTags != null ? postLoadEntitiesComponent.loadUniqueTagsFromDatabase(newTags) : null);
    }

}
