package org.kiwiproject.migrations.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.core.Configuration;
import io.mongock.driver.api.driver.ConnectionDriver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MongoMigrationConfiguration")
class MongoMigrationConfigurationTest {

    @Test
    void shouldReturnFalseAsDefaultForDisablingTransactions() {
        var mongoMigrationConfiguration = buildSampleMongoMigrationConfiguration();

        var config = new Configuration();
        assertThat(mongoMigrationConfiguration.shouldDisableTransactions(config)).isFalse();
    }

    @Test
    void shouldReturnDefaultCommandName() {
        var mongoMigrationConfiguration = buildSampleMongoMigrationConfiguration();

        assertThat(mongoMigrationConfiguration.getCommandName()).isEqualTo(MongoMigrationConfiguration.DEFAULT_NAME);
    }

    private MongoMigrationConfiguration<Configuration> buildSampleMongoMigrationConfiguration() {
        return new MongoMigrationConfiguration<>() {
            @Override
            public String getMigrationPackage(Configuration config) {
                return null;
            }

            @Override
            public String getMongoUri(Configuration config) {
                return null;
            }

            @Override
            public String getDatabaseName(Configuration config) {
                return null;
            }

            @Override
            public ConnectionDriver getConnectionDriver(Configuration config) {
                return null;
            }
        };
    }
}
