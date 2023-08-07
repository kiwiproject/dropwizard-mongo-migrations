package org.kiwiproject.migrations.mongo.samples.mongodatabase;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

@ChangeUnit(id = "changeWithMongoDatabase", order = "1", author = "crohr", transactional = false)
public class SimpleMigration {

    /**
     * All DDL actions (collection creation, index creation etc.) need to be done in BeforeExecution especially if
     * using transactions.
     */
    @BeforeExecution
    public void beforeExecution(MongoDatabase db) {
        db.createCollection("myCollection");
    }

    /**
     * This will roll back the DDL actions if the migration fails.
     */
    @RollbackBeforeExecution
    public void rollbackExecution(MongoDatabase db) {
        db.getCollection("myCollection").drop();
    }

    /**
     * This is the actual migration work.
     */
    @Execution
    public void execution(MongoDatabase db) {
        MongoCollection<Document> myCollection = db.getCollection("myCollection");
        var doc = new Document("testName", "example").append("test", "1");
        myCollection.insertOne(doc);
    }

    /**
     * This is the process of rolling back if the migration fails (and one day when Mongock adds UNDO functionality).
     */
    @RollbackExecution
    public void rollback(MongoDatabase db) {
        MongoCollection<Document> myCollection = db.getCollection("myCollection");
        myCollection.deleteOne(new Document("testName", "example"));
    }

}
