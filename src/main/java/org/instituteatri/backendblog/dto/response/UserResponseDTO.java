package org.instituteatri.backendblog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public record UserDTO(
        String id,
        String name,
        String lastName,
        String phoneNumber,
        String bio,
        @JsonIgnore
        List<PostDTO> postDTOS) {
}
