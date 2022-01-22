package org.kiwiproject.migrations.mongo;

import com.mongodb.client.MongoClients;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.mongock.driver.mongodb.v3.driver.MongoCore3Driver;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.standalone.MongockStandalone;
import net.sourceforge.argparse4j.inf.Namespace;

public abstract class AbstractMongockCommand<T extends Configuration> extends ConfiguredCommand<T> {
    private final Class<T> configurationClass;
    private final MongoMigrationConfiguration<T> migrationConfiguration;

    protected AbstractMongockCommand(String name, String description, MongoMigrationConfiguration<T> migrationConfiguration, Class<T> configurationClass) {
        super(name, description);
        this.configurationClass = configurationClass;
        this.migrationConfiguration = migrationConfiguration;
    }

    @Override
    protected Class<T> getConfigurationClass() {
        return configurationClass;
    }

    @Override
    protected void run(Bootstrap<T> bootstrap, Namespace namespace, T configuration) {
        var mongoClient = MongoClients.create(migrationConfiguration.getMongoUri(configuration));

        var driver = MongoCore3Driver.withDefaultLock(mongoClient, migrationConfiguration.getDatabaseName(configuration));
        var runner = MongockStandalone.builder()
                .setDriver(driver)
                .addMigrationScanPackage(migrationConfiguration.getMigrationPackage(configuration))
                .buildRunner();

        run(namespace, runner);
    }

    protected abstract void run(Namespace namespace, MongockRunner mongock);
}
