package org.kiwiproject.migrations.mongo;

public class TestMongoMigrationConfiguration implements MongoMigrationConfiguration<TestMigrationConfiguration> {

    private final String mongoUri;
    private final String databaseName;

    public TestMongoMigrationConfiguration(String mongoUri, String databaseName) {
        this.mongoUri = mongoUri;
        this.databaseName = databaseName;
    }

    @Override
    public String getMigrationPackage(TestMigrationConfiguration config) {
        return "org.kiwiproject.migrations.mongo.samples";
    }

    @Override
    public String getMongoUri(TestMigrationConfiguration config) {
        return mongoUri;
    }

    @Override
    public String getDatabaseName(TestMigrationConfiguration config) {
        return databaseName;
    }
}
