package org.kiwiproject.migrations.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MongoMigrationConfiguration")
class MongoMigrationConfigurationTest {

    @Test
    void shouldReturnFalseAsDefaultForDisablingTransactions() {
        var mongoMigrationConfiguration = new MongoMigrationConfiguration<Configuration>() {
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
        };

        var config = new Configuration();
        assertThat(mongoMigrationConfiguration.shouldDisableTransactions(config)).isFalse();
    }
}
