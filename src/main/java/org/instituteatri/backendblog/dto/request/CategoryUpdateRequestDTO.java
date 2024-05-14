package org.instituteatri.backendblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequestDTO(
        @NotBlank(message = "Name is required.")
        @Size(max = 50, message = "Name cannot be longer than 50 characters.")
        String name,

        @NotBlank(message = "Slug is required.")
        @Size(max = 50, message = "Slug cannot be longer than 50 characters.")
        String slug) {
}
