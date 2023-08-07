package org.kiwiproject.migrations.mongo;

import com.mongodb.client.MongoClients;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import org.springframework.data.mongodb.core.MongoTemplate;

public class TestMongoMigrationConfiguration implements MongoMigrationConfiguration<TestMigrationConfiguration> {

    private final String mongoUri;
    private final String databaseName;
    private final String migrationPackage;

    public TestMongoMigrationConfiguration(String mongoUri, String databaseName, String migrationPackage) {
        this.mongoUri = mongoUri;
        this.databaseName = databaseName;
        this.migrationPackage = migrationPackage;
    }

    @Override
    public String getMigrationPackage(TestMigrationConfiguration config) {
        return migrationPackage;
    }

    @Override
    public String getMongoUri(TestMigrationConfiguration config) {
        return mongoUri;
    }

    @Override
    public String getDatabaseName(TestMigrationConfiguration config) {
        return databaseName;
    }

    @Override
    public boolean shouldDisableTransactions(TestMigrationConfiguration config) {
        return true;
    }

    @Override
    public ConnectionDriver getConnectionDriver(TestMigrationConfiguration config) {
        var mongoClient = MongoClients.create(mongoUri);
        var mongoTemplate = new MongoTemplate(mongoClient, databaseName);
        return SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
    }
}
