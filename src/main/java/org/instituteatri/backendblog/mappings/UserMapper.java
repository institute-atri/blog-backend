package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dto.request.RegisterRequestDTO;
import org.instituteatri.backendblog.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User updateUserFromDto(RegisterRequestDTO dto, User existingUser);

    UserResponseDTO toUserDto(User user);
}
