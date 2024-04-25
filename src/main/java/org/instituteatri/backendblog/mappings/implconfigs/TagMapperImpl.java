package org.instituteatri.backendblog.mappings.implconfigs;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.dtos.TagDTO;
import org.instituteatri.backendblog.mappings.TagMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class TagMapperImpl implements TagMapper {

    private final MapPostsImplToDTO mapPostsImplToDTO;

    @Override
    public TagDTO toTagDto(Tag tag) {
        if (tag == null) {
            return null;
        }

        List<PostDTO> postDTOs = mapPostsImplToDTO.mapPostsToDTO(tag.getPosts());

        return new TagDTO(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                postDTOs
        );
    }
}
