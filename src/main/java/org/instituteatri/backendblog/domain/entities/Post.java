package org.instituteatri.backendblog.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.instituteatri.backendblog.dtos.AuthorDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post{
    private String id;
    private String title;
    private String summary;
    private String body;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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


    public void UpdatedPost(String title, String summary, String body, String slug, LocalDateTime updatedAt) {
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.slug = slug;
        this.updatedAt = updatedAt;
    }
}
