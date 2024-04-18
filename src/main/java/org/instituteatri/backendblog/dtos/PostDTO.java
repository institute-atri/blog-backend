package org.instituteatri.backendblog.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Comment;
import org.instituteatri.backendblog.domain.entities.Tag;

import java.util.List;

public record PostDTO(
        @NotBlank(message = "Title is required.")
        @Size(min = 5, max = 30, message = "Title must be between 5 and 30 characters.")
        String title,

        @NotBlank(message = "Summary is required.")
        @Size(min = 5, max = 100, message = "Summary must be between 5 and 100 characters.")
        String summary,

        @NotBlank(message = "Body is required.")
        @Size(min = 5, max = 1000, message = "Body must be between 5 and 1000 characters.")
        String body,

        @NotBlank(message = "Slug is required.")
        @Size(min = 3, max = 50, message = "Slug must be between 3 and 50 characters.")
        String slug,

        List<Category> categories,
        List<Tag> tags,
        List<Comment> comments
) {
}

