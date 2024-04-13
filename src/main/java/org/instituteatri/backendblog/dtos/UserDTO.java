package org.instituteatri.backendblog.dtos;

import org.instituteatri.backendblog.domain.entities.UserRole;

public record UserDTO(
        String name,
        String lastName,
        String phoneNumber,
        String bio,
        String email,
        String password,
        UserRole role){
}
