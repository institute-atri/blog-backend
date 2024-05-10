package org.instituteatri.backendblog.mappings.implconfigs;

import lombok.RequiredArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;
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
    public CategoryRequestDTO toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        List<PostRequestDTO> postRequestDTOS = mapPostsImplToDTO.mapPostsToDTO(category.getPosts());

        return new CategoryRequestDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                postRequestDTOS
        );
    }
}
