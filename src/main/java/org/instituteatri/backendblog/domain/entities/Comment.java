package org.instituteatri.backendblog.domain.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.instituteatri.backendblog.dto.response.AuthorResponseDTO;

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

    private transient AuthorResponseDTO authorResponseDTO;

    public Comment(String text, LocalDateTime createdAt, User user) {
        this.text = text;
        this.createdAt = createdAt;
        this.user = user;
        if (user != null) {
            this.authorResponseDTO = new AuthorResponseDTO(user.getName(), user.getLastName());
        }
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
