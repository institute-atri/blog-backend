package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
}
