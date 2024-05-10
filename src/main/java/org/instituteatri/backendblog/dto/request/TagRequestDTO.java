package org.instituteatri.backendblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagRequestDTO {
    @NotBlank(message = "Name is required.")
    @Size(max = 10, message = "Name cannot be longer than 10 characters.")
    private String name;
    @NotBlank(message = "Slug is required.")
    @Size(max = 50, message = "Slug cannot be longer than 50 characters.")
    private String slug;
}
