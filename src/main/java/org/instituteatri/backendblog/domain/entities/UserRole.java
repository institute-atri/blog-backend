package org.instituteatri.backendblog.domain.entities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");
    private String role;
}
