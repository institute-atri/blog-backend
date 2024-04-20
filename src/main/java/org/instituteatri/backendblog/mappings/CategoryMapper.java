package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.dtos.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryDTO toCategoryDto(Category category);
}