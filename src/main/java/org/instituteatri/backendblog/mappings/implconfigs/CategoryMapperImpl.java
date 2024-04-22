package org.instituteatri.backendblog.mappings.implconfigs;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.dtos.CategoryDTO;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.mappings.CategoryMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDTO toCategoryDto(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getPosts().stream().map(post -> new PostDTO(
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
                )).toList()
        );
    }
}
