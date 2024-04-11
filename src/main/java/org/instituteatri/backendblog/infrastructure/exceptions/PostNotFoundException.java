package org.instituteatri.backendblog.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostNotFoundException extends RuntimeException {
    private String id;

    public PostNotFoundException(String id) {
        super("Post not found with id: " + id);
        this.id = id;
    }

}
