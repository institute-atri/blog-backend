package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    UserDetails findByEmail(String email);
}
