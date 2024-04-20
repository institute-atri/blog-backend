package org.instituteatri.backendblog.service.components.postComponents;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.CategoryNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.TagNotFoundException;
import org.instituteatri.backendblog.infrastructure.exceptions.UserAccessDeniedException;
import org.instituteatri.backendblog.repository.CategoryRepository;
import org.instituteatri.backendblog.repository.TagRepository;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostDeleteComponent {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;


    public void decrementTagPostCountComponent(String tagId, String postId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
        tag.setPostCount(tag.getPostCount() - 1);
        tag.getPosts().removeIf(post -> post.getId().equals(postId));
        tagRepository.save(tag);
    }

    public void decrementCategoryPostCountComponent(String categoryId, String postId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        category.setPostCount(category.getPostCount() - 1);
        category.getPosts().removeIf(post -> post.getId().equals(postId));
        categoryRepository.save(category);
    }

    public void decrementPostCountComponent(User currentUser) {
        int currentPostUser = currentUser.getPostCount();
        currentUser.setPostCount(currentPostUser - 1);
        userRepository.save(currentUser);
    }
    public void validatePostDeleteComponent(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new UserAccessDeniedException();
        }
    }
}
