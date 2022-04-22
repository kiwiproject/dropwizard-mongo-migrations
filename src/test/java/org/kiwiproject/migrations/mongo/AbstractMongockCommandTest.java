package org.kiwiproject.migrations.mongo;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.kiwiproject.test.junit.jupiter.params.provider.AsciiOnlyBlankStringSource;

import io.dropwizard.setup.Bootstrap;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.MongockRunner;
import net.sourceforge.argparse4j.inf.Namespace;

class AbstractMongockCommandTest {

    static class BadMongoMigrationConfiguration implements MongoMigrationConfiguration<TestMigrationConfiguration> {

        private final ConnectionDriver driver;
        private final String migrationPackage;

        public BadMongoMigrationConfiguration(ConnectionDriver driver, String migrationPackage) {
            this.driver = driver;
            this.migrationPackage = migrationPackage;
        }

        @Override
        public String getMigrationPackage(TestMigrationConfiguration config) {
            return migrationPackage;
        }

        @Override
        public String getMongoUri(TestMigrationConfiguration config) {
            return null;
        }

        @Override
        public String getDatabaseName(TestMigrationConfiguration config) {
            return null;
        }

        @Override
        public ConnectionDriver getConnectionDriver(TestMigrationConfiguration config) {
            return driver;
        }
    }

    static class TestAbstractMongockCommand extends AbstractMongockCommand<TestMigrationConfiguration> {

        protected TestAbstractMongockCommand(String name,
                MongoMigrationConfiguration<TestMigrationConfiguration> migrationConfiguration,
                Class<TestMigrationConfiguration> configurationClass) {

            super(name, "Run database migration tasks", migrationConfiguration, configurationClass);
        }

        @Override
        protected void run(Namespace namespace, MongockRunner mongock) {
            // no-op
        }
    }

    @Nested
    class RunMethod {

        private Bootstrap<TestMigrationConfiguration> bootstrap;
        private Namespace namespace;

        @BeforeEach
        @SuppressWarnings("unchecked")
        void setUp() {
            bootstrap = mock(Bootstrap.class);
            namespace = mock(Namespace.class);
        }

        @Test
        void shouldNotAllowNullConnectionDriver() {
            var config = new BadMongoMigrationConfiguration(null, "com.acme.mongo.migration");
            var command = new TestAbstractMongockCommand("db", config, TestMigrationConfiguration.class);

            assertThatIllegalStateException()
                    .isThrownBy(() -> command.run(bootstrap, namespace, new TestMigrationConfiguration()))
                    .withMessage("connectionDriver must not be null");
        }

        @ParameterizedTest
        @AsciiOnlyBlankStringSource
        void shouldNotAllowBlankMigrationPackage(String migrationPackage) {
            var config = new BadMongoMigrationConfiguration(mock(ConnectionDriver.class), migrationPackage);
            var command = new TestAbstractMongockCommand("db", config, TestMigrationConfiguration.class);

            assertThatIllegalStateException()
                    .isThrownBy(() -> command.run(bootstrap, namespace, new TestMigrationConfiguration()))
                    .withMessage("migrationPackage must not be blank");
        }
    }
}
