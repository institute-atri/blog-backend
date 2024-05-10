package org.instituteatri.backendblog.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.instituteatri.backendblog.dto.request.PostRequestDTO;

import java.util.List;

public record UserResponseDTO(
        String id,
        String name,
        String lastName,
        String phoneNumber,
        String bio,
        @JsonIgnore
        List<PostRequestDTO> postRequestDTOS) {
}
