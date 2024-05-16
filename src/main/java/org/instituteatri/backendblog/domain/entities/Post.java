package org.instituteatri.backendblog.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Post implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String title;
    private String summary;
    private String body;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @DBRef(lazy = true)
    @JsonIgnore
    private User user;

    private List<Category> categories = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();

    private transient AuthorResponseDTO authorResponseDTO;
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String summary, String body, String slug, LocalDateTime createdAt, User user) {
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.slug = slug;
        this.createdAt = createdAt;
        this.user = user;
        this.authorResponseDTO = new AuthorResponseDTO(user.getName(), user.getLastName());
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (this.createdAt != null) {
            throw new UnsupportedOperationException("createdAt cannot be updated after object creation");
        }
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        if (updatedAt.isBefore(this.createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }
        this.updatedAt = updatedAt;
    }
}
