package org.instituteatri.backendblog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Comment;
import org.instituteatri.backendblog.domain.entities.Tag;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDTO {

    private String id;
    @NotBlank(message = "Title is required.")
    @Size(min = 5, max = 30, message = "Title must be between 5 and 30 characters.")
    private String title;

    @NotBlank(message = "Summary is required.")
    @Size(min = 5, max = 100, message = "Summary must be between 5 and 100 characters.")
    private String summary;

    @NotBlank(message = "Body is required.")
    @Size(min = 5, max = 1000, message = "Body must be between 5 and 1000 characters.")
    private String body;

    @NotBlank(message = "Slug is required.")
    @Size(min = 3, max = 50, message = "Slug must be between 3 and 50 characters.")
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorResponseDTO authorResponseDTO;
    private List<Category> categories;
    private List<Tag> tags;
    private List<Comment> comments;
}

