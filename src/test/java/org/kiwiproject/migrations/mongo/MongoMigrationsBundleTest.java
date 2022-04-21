package org.kiwiproject.migrations.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClients;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v3.SpringDataMongoV3Driver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

class MongoMigrationsBundleTest {

    @RegisterExtension
    static final MongoServerExtension MONGO_SERVER_EXTENSION = new MongoServerExtension();

    private final MongoMigrationsBundle<TestMigrationConfiguration> migrationsBundle = new MongoMigrationsBundle<>() {

        @Override
        public String getMigrationPackage(TestMigrationConfiguration config) {
            return "org.kiwiproject.migrations.mongo.samples";
        }

        @Override
        public String getMongoUri(TestMigrationConfiguration config) {
            return MONGO_SERVER_EXTENSION.getConnectionString();
        }

        @Override
        public String getDatabaseName(TestMigrationConfiguration config) {
            return MONGO_SERVER_EXTENSION.getTestDatabaseName();
        }

        @Override
        public ConnectionDriver getConnectionDriver(TestMigrationConfiguration config) {
            var mongoClient = MongoClients.create(getMongoUri(config));
            var mongoTemplate = new MongoTemplate(mongoClient, getDatabaseName(config));
            return SpringDataMongoV3Driver.withDefaultLock(mongoTemplate);
        }
    };

    private final Application<TestMigrationConfiguration> application = new Application<>() {

        @Override
        public void run(TestMigrationConfiguration testMigrationConfiguration, Environment environment) {
        }
    };

    @Test
    void testMigrationsBundle() {
        var bootstrap = new Bootstrap<>(application);
        assertThat(migrationsBundle.name()).isEqualTo("db");

        migrationsBundle.initialize(bootstrap);

        assertThat(bootstrap.getCommands())
                .hasSize(1)
                .satisfies(list -> assertThat(list.get(0)).isInstanceOf(DbCommand.class));
    }

}
