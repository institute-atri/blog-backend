package org.instituteatri.backendblog.service.helpers.helpPost;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.repository.PostRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HelperComponentPostUpdate {

    private final PostRepository postRepository;
    private final HelperComponentLoadCategories helperLoadCategories;
    private final HelperComponentLoadTags helperLoadTags;

    public void helperUpdate(String id, PostDTO updatedPostDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        updatePostData(existingPost, updatedPostDto);
        postRepository.save(existingPost);
    }

    public void helperValidateAuthorship(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new DomainAccessDeniedException();
        }
    }

    private void updatePostData(Post existingPost, PostDTO updatedPostDto) {
        helperUpdateTitle(existingPost, updatedPostDto.title());
        helperUpdateSummary(existingPost, updatedPostDto.summary());
        helperUpdateBody(existingPost, updatedPostDto.body());
        helperUpdateSlug(existingPost, updatedPostDto.slug());
        existingPost.setUpdatedAt(LocalDateTime.now());
        helperUpdateCategories(existingPost, updatedPostDto.categories());
        helperUpdateTags(existingPost, updatedPostDto.tags());
    }

    private void helperUpdateTitle(Post existingPost, String newTitle) {
        if (newTitle != null && !newTitle.equals(existingPost.getTitle())) {
            existingPost.setTitle(newTitle);
        }
    }

    private void helperUpdateSummary(Post existingPost, String newSummary) {
        if (newSummary != null && !newSummary.equals(existingPost.getSummary())) {
            existingPost.setSummary(newSummary);
        }
    }

    private void helperUpdateBody(Post existingPost, String newBody) {
        if (newBody != null && !newBody.equals(existingPost.getBody())) {
            existingPost.setBody(newBody);
        }
    }

    private void helperUpdateSlug(Post existingPost, String newSlug) {
        if (newSlug != null && !newSlug.equals(existingPost.getSlug())) {
            existingPost.setSlug(newSlug);
        }
    }

    private void helperUpdateCategories(Post existingPost, List<Category> newCategories) {
        if (newCategories != null) {
            existingPost.setCategories(helperLoadCategories.loadCategories(newCategories));
        }
    }

    private void helperUpdateTags(Post existingPost, List<Tag> newTags) {
        if (newTags != null) {
            existingPost.setTags(helperLoadTags.loadTags(newTags));
        }
    }
}
