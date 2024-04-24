package org.instituteatri.backendblog.domain.token;


import lombok.*;
import org.instituteatri.backendblog.domain.entities.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String token;

    public TokenType tokenType = TokenType.BEARER;

    private User user;

    public boolean revoked;

    public boolean expired;
}
