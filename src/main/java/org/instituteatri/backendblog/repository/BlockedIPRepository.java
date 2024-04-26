package org.instituteatri.backendblog.repository;

import org.instituteatri.backendblog.domain.entities.BlockedIP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedIPRepository extends MongoRepository<BlockedIP, String> {
    BlockedIP findByIpAddress(String ipAddress);
}
