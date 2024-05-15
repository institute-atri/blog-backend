package org.instituteatri.backendblog.domain.token;


import lombok.*;
import org.instituteatri.backendblog.domain.entities.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String tokenValue;

    private TokenType tokenType = TokenType.BEARER;

    private User user;

    private boolean revoked;

    private boolean expired;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return expired == token.expired &&
                revoked == token.revoked &&
                Objects.equals(id, token.id) &&
                Objects.equals(tokenValue, token.tokenValue) &&
                tokenType == token.tokenType &&
                Objects.equals(user, token.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tokenValue, tokenType, user, expired, revoked);
    }
}
