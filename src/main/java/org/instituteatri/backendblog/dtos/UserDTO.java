package org.instituteatri.backendblog.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserDTO(
        String id,
        @NotBlank(message = "Name is required.")
        @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters.")
        String name,
        @NotBlank(message = "Last name is required.")
        @Size(min = 5, max = 30, message = "Last name must be between 5 and 30 characters.")
        String lastName,

        @NotBlank(message = "Phone number is required.")
        @Size(max = 11, message = "Phone number must be less than 11 characters.")
        @Pattern(regexp = "^[0-9]+$", message = "Phone number must contain only digits.")
        String phoneNumber,

        @Size(max = 100, message = "Bio must be less than 100 characters.")
        String bio,
        @JsonIgnore
        List<PostDTO> postDTOS
) {
}
