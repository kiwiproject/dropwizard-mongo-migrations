package org.kiwiproject.migrations.mongo.samples.mongotemplate;

import static org.kiwiproject.collect.KiwiMaps.newHashMap;

import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@ChangeUnit(id = "changeWithMongoTemplate", order = "1", author = "crohr", transactional = false)
public class SimpleMigration {

    /**
     * All DDL actions (collection creation, index creation, etc.) need to be done in BeforeExecution especially if
     * using transactions.
     */
    @BeforeExecution
    public void beforeExecution(MongoTemplate template) {
        template.createCollection("myTemplateCollection");
    }

    /**
     * This will roll back the DDL actions if the migration fails.
     */
    @RollbackBeforeExecution
    public void rollbackExecution(MongoTemplate template) {
        template.dropCollection("myTemplateCollection");
    }

    /**
     * This is the actual migration work.
     */
    @Execution
    public void execution(MongoTemplate template) {
        template.insert(newHashMap("testName", "example", "test", "1"), "myTemplateCollection");
    }

    /**
     * This is the process of rolling back if the migration fails (and one day when Mongock adds UNDO functionality).
     */
    @RollbackExecution
    public void rollback(MongoTemplate template) {
        var query = Query.query(Criteria.where("testName").is("example"));
        template.remove(query, "myTemplateCollection");
    }

}
