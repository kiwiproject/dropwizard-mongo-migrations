package org.kiwiproject.migrations.mongo;

public interface MongoMigrationConfiguration<T> {
    String getMigrationPackage(T config);
    String getMongoUri(T config);
    String getDatabaseName(T config);

    default boolean shouldDisableTransactions(T config) {
        return false;
    }
}
