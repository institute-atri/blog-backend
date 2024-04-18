package org.instituteatri.backendblog.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.instituteatri.backendblog.dtos.AuthorDTO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @JsonIgnore
    private User user;

    private AuthorDTO authorDTO;

    public Comment(String text, LocalDateTime createdAt, User user) {
        this.text = text;
        this.createdAt = createdAt;
        this.authorDTO = new AuthorDTO(user.getName(), user.getLastName());
    }
}
