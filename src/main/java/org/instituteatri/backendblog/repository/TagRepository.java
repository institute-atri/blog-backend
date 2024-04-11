package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
}
