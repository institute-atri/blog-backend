package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    void createPostFromDto(PostDTO dto, Post existingPost);

    PostDTO toPostDto(Post post);
}