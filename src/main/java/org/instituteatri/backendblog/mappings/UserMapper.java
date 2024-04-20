package org.instituteatri.backendblog.mappings;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.RegisterDTO;
import org.instituteatri.backendblog.dtos.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User updateUserFromDto(RegisterDTO dto, User existingUser);

    UserDTO toUserDto(User user);
}
