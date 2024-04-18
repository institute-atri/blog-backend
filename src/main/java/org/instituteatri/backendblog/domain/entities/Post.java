package org.instituteatri.backendblog.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.instituteatri.backendblog.dtos.AuthorDTO;
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

    private AuthorDTO authorDTO;
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String summary, String body, String slug, LocalDateTime createdAt, User user) {
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.slug = slug;
        this.createdAt = createdAt;
        this.user = user;
        this.authorDTO = new AuthorDTO(user.getName(), user.getLastName());
    }
}
