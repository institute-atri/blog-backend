package org.instituteatri.backendblog.mappings.implconfigs;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MapPostsImplToDTO {

    public List<PostDTO> mapPostsToDTO(List<Post> posts) {
        return posts != null
                ? posts.stream()
                .filter(Objects::nonNull)
                .map(post -> new PostDTO(
                        post.getId(),
                        post.getTitle(),
                        post.getSummary(),
                        post.getBody(),
                        post.getSlug(),
                        post.getCreatedAt(),
                        post.getUpdatedAt(),
                        post.getAuthorDTO(),
                        post.getCategories(),
                        post.getTags(),
                        post.getComments()
                )).toList() : null;
    }
}
