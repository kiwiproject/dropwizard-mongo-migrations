package org.kiwiproject.migrations.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kiwiproject.migrations.mongo.MongoTestContainerHelpers.newMongoDBContainer;

import com.mongodb.client.MongoClients;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v4.SpringDataMongoV4Driver;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

@Testcontainers(disabledWithoutDocker = true)
class MongoMigrationsBundleTest {

    @Container
    static final MongoDBContainer MONGODB = newMongoDBContainer();

    private final MongoMigrationsBundle<TestMigrationConfiguration> migrationsBundle = new MongoMigrationsBundle<>() {

        @Override
        public String getMigrationPackage(TestMigrationConfiguration config) {
            return "org.kiwiproject.migrations.mongo.samples";
        }

        @Override
        public String getMongoUri(TestMigrationConfiguration config) {
            return MONGODB.getConnectionString();
        }

        @Override
        public String getDatabaseName(TestMigrationConfiguration config) {
            return "test";
        }

        @Override
        public ConnectionDriver getConnectionDriver(TestMigrationConfiguration config) {
            var mongoClient = MongoClients.create(getMongoUri(config));
            var mongoTemplate = new MongoTemplate(mongoClient, getDatabaseName(config));
            return SpringDataMongoV4Driver.withDefaultLock(mongoTemplate);
        }
    };

    private final Application<TestMigrationConfiguration> application = new Application<>() {

        @Override
        public void run(TestMigrationConfiguration testMigrationConfiguration, Environment environment) {
            // intentionally empty
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
