package org.instituteatri.backendblog.service.helpers.helpPost;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.instituteatri.backendblog.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelperComponentPostDelete {
    
    private final UserRepository userRepository;
    public void helperValidatePostDeletion(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new DomainAccessDeniedException();
        }
    }
    public void helperDecrementPostCount(User currentUser) {
        int currentPostUser = currentUser.getPostCount();
        currentUser.setPostCount(currentPostUser - 1);
        userRepository.save(currentUser);
    }
}
