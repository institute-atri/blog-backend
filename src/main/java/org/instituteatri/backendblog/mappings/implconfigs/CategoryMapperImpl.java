package org.instituteatri.backendblog.mappings.implconfigs;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.dtos.CategoryDTO;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.mappings.CategoryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class CategoryMapperImpl implements CategoryMapper {

    private final MapPostsImplToDTO mapPostsImplToDTO;


    @Override
    public CategoryDTO toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        List<PostDTO> postDTOs = mapPostsImplToDTO.mapPostsToDTO(category.getPosts());

        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                postDTOs
        );
    }
}
