package org.instituteatri.backendblog.infrastructure.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundException  extends RuntimeException {
    private String id;

    public UserNotFoundException(String id) {
        super("User not found with id: " + id);
        this.id = id;
    }
}
