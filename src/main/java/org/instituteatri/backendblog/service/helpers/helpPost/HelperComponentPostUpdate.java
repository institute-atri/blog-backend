package org.instituteatri.backendblog.service.helpers.helpPost;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.instituteatri.backendblog.infrastructure.exceptions.PostNotFoundException;
import org.instituteatri.backendblog.repository.PostRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HelperComponentPostUpdate {

    private final PostRepository postRepository;

    public void helperUpdate(String id, Post updatedPost) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        helperUpdateTitle(existingPost, updatedPost.getTitle());
        helperUpdateSummary(existingPost,updatedPost.getSummary());
        helperUpdateBody(existingPost, updatedPost.getBody());
        helperUpdateSlug(existingPost, updatedPost.getSlug());

        existingPost.setUpdatedAt(LocalDateTime.now());

        postRepository.save(existingPost);
    }

    public void helperValidateAuthorship(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new DomainAccessDeniedException();
        }
    }

    public void helperUpdatedPost(Post post) {
        post.UpdatedPost(
                post.getTitle(),
                post.getSummary(),
                post.getBody(),
                post.getSlug(),
                LocalDateTime.now());
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
}
