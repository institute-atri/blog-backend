package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.Post;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    void createPostFromDto(PostRequestDTO dto, Post existingPost);

    PostRequestDTO toPostDto(Post post);
}