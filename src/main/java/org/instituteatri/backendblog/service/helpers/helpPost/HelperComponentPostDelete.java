package org.instituteatri.backendblog.service.helpers.helpPost;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.infrastructure.exceptions.DomainAccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class HelperComponentPostDelete {

    public void helperValidatePostDeletion(Post existingPost, User currentUser) {
        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new DomainAccessDeniedException();
        }
    }
}
