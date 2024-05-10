package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dto.request.TagRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    TagRequestDTO toTagDto(Tag tag);
}
