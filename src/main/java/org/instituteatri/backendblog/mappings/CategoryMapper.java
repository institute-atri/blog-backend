package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.dto.request.CategoryRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryRequestDTO toCategoryDto(Category category);
}