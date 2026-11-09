package com.santea.admin;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class AdminSequenceService {
    private final MongoTemplate mongoTemplate;

    public AdminSequenceService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Long nextId() {
        AdminSequence sequence = mongoTemplate.findAndModify(
                Query.query(Criteria.where("_id").is("admin-user")),
                new Update().inc("value", 1),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                AdminSequence.class
        );
        if (sequence == null) {
            throw new IllegalStateException("Could not allocate admin user ID");
        }
        return sequence.getValue();
    }
}
