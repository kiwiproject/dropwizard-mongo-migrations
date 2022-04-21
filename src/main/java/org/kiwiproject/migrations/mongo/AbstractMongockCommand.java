package org.kiwiproject.migrations.mongo;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.standalone.MongockStandalone;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;

@Slf4j
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
        var driver = migrationConfiguration.getConnectionDriver(configuration);
        checkState(nonNull(driver), "connectionDriver must not be null");

        var migrationPackage = migrationConfiguration.getMigrationPackage(configuration);
        checkState(isNotBlank(migrationPackage), "migrationPackage must not be blank");

        LOG.info("Using driver {} and migrationPackage {}", driver.getClass(), migrationPackage);

        var runner = MongockStandalone.builder()
                .setDriver(driver)
                .addMigrationScanPackage(migrationPackage)
                .buildRunner();

        run(namespace, runner);
    }

    protected abstract void run(Namespace namespace, MongockRunner mongock);

}
