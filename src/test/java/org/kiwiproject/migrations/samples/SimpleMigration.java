package org.kiwiproject.migrations.samples;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

@ChangeLog(order = "001")
public class SimpleMigration {

    @ChangeSet(order = "001", id = "changeWithMongoDatabase", author = "crohr")
    public void changeWithMongoDatabase(MongoDatabase db) {
        System.out.println("I'm in the MIGRATION");
        MongoCollection<Document> myCollection = db.getCollection("myCollection");
        var doc = new Document("testName", "example").append("test", "1");
        myCollection.insertOne(doc);
    }

}
