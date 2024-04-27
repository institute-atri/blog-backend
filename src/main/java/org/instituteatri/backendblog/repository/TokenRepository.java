package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.token.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<Token, String> {

    @Query("{'user.id': ?0, $or: [{'expired': false}, {'revoked': false}]}")
    List<Token> findAllValidTokenByUser(String id);
    List<Token> findAllByUserId(String userId);
    Optional<Token> findByTokenValue(String token);
}
