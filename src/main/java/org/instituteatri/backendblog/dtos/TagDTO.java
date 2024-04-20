package org.instituteatri.backendblog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TagDTO(

        String id,
        @NotBlank(message = "Name is required.")
        @Size(max = 10, message = "Name cannot be longer than 10 characters.")
        String name,
        @NotBlank(message = "Slug is required.")
        @Size(max = 50, message = "Slug cannot be longer than 50 characters.")
        String slug,
        @JsonIgnore
        List<PostDTO> postDTOS) {
}
