package org.kiwiproject.migrations.mongo;

import io.mongock.driver.api.driver.ConnectionDriver;

public interface MongoMigrationConfiguration<T> {

    String DEFAULT_NAME = "db";

    String getMigrationPackage(T config);
    String getMongoUri(T config);
    String getDatabaseName(T config);
    ConnectionDriver getConnectionDriver(T config);

    default boolean shouldDisableTransactions(T config) {
        return false;
    }

    default String getCommandName() {
        return DEFAULT_NAME;
    }
}
