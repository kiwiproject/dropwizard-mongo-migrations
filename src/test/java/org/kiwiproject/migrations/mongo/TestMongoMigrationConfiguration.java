package org.kiwiproject.migrations.mongo;

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
}
