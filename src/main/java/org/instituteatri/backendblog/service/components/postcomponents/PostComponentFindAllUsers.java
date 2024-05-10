package org.instituteatri.backendblog.service.components.postcomponents;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostComponentFindAllUsers {
    public void updatePostTagsWithUpdatedEntities(Post post, List<Tag> updatedTags) {
        post.setTags(post.getTags().stream()
                .filter(tag -> updatedTags.stream().anyMatch(updatedTag -> updatedTag.getId().equals(tag.getId())))
                .map(tag -> updatedTags.stream()
                        .filter(updatedTag -> updatedTag.getId().equals(tag.getId()))
                        .findFirst()
                        .orElse(tag))
                .toList());
    }

    public void updatePostCategoriesWithUpdatedEntities(Post post, List<Category> updatedCategories) {
        post.setCategories(post.getCategories().stream()
                .filter(category -> updatedCategories.stream().anyMatch(updatedCategory -> updatedCategory.getId().equals(category.getId())))
                .map(category -> updatedCategories.stream()
                        .filter(updatedCategory -> updatedCategory.getId().equals(category.getId()))
                        .findFirst()
                        .orElse(category))
                .toList());
    }

    public void updatePostAuthorWithUpdatedUser(Post post, List<User> updatedUsers) {
        User updatedUser = updatedUsers.stream()
                .filter(user -> user.getId().equals(post.getUser().getId()))
                .findFirst()
                .orElse(post.getUser());

        post.setAuthorResponseDTO(new AuthorResponseDTO(updatedUser.getName(), updatedUser.getLastName()));
        post.setUser(updatedUser);
    }
}
