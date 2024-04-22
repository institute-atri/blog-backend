package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.token.InvalidToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvalidTokenRepository extends MongoRepository<InvalidToken, String> {

    boolean existsByToken(String token);
}
