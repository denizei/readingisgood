package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.ChangeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeLogRepository extends MongoRepository<ChangeLog, String> {

}
