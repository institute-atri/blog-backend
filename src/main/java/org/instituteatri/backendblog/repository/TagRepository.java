package myapp.authenticateAPI.repository;

import myapp.authenticateAPI.domain.entities.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
}
