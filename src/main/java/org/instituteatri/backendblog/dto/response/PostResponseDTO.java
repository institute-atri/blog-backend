package org.instituteatri.backendblog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.instituteatri.backendblog.domain.entities.Category;
import org.instituteatri.backendblog.domain.entities.Comment;
import org.instituteatri.backendblog.domain.entities.Tag;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
    private String id;
    private String title;
    private String summary;
    private String body;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuthorResponseDTO authorResponseDTO;
    private List<Category> categories;
    private List<Tag> tags;
    private List<Comment> comments;
}
