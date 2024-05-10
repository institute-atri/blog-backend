package org.instituteatri.backendblog.mappings.implconfigs;

import org.instituteatri.backendblog.domain.entities.User;
import org.instituteatri.backendblog.dtos.PostDTO;
import org.instituteatri.backendblog.dtos.RegisterDTO;
import org.instituteatri.backendblog.dtos.UserDTO;
import org.instituteatri.backendblog.mappings.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Component
public class UserMapperImpl implements UserMapper {


    @Override
    public User updateUserFromDto(RegisterDTO dto, User existingUser) {
        existingUser.setName(dto.name());
        existingUser.setLastName(dto.lastName());
        existingUser.setPhoneNumber(dto.phoneNumber());
        existingUser.setBio(dto.bio());
        existingUser.setEmail(dto.email());
        existingUser.setPassword(dto.password());

        return existingUser;
    }

    @Override
    public UserDTO toUserDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getBio(),
                user.getPosts().stream().map(post -> new PostDTO(
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
