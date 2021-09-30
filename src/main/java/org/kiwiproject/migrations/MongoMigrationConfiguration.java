package org.kiwiproject.migrations;

public interface MongoMigrationConfiguration<T> {
    String getMigrationPackage(T config);
    String getMongoUri(T config);
    String getDatabaseName(T config);
}
