package org.instituteatri.backendblog.mappings.implconfigs;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.mappings.PostMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public class PostMapperImpl implements PostMapper {
    @Override
    public void createPostFromDto(PostDTO dto, Post existingPost) {
        existingPost.setTitle(dto.title());
        existingPost.setSummary(dto.summary());
        existingPost.setBody(dto.body());
        existingPost.setSlug(dto.slug());
        existingPost.setCreatedAt(LocalDateTime.now());

        existingPost.setAuthorDTO(dto.authorDTO());
        existingPost.setCategories(dto.categories());
        existingPost.setTags(dto.tags());
        existingPost.setComments(dto.comments());
    }

    @Override
    public PostDTO toPostDto(Post post) {
        return new PostDTO(
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
        );
    }
}
