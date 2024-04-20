package org.instituteatri.backendblog.mappings.implconfigs;

import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.dtos.TagDTO;
import org.instituteatri.backendblog.mappings.TagMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class TagMapperImpl implements TagMapper {

    @Override
    public TagDTO toTagDto(Tag tag) {
        return new TagDTO(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getPosts().stream().map(post -> new PostDTO(
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
                )).collect(Collectors.toList())
        );
    }
}
