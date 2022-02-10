package org.kiwiproject.migrations.mongo;

import static org.kiwiproject.reflect.KiwiReflection.findMethod;
import static org.kiwiproject.reflect.KiwiReflection.invokeExpectingReturn;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.standalone.MongockStandalone;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Optional;

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
        var mongoClient = MongoClients.create(migrationConfiguration.getMongoUri(configuration));

        var driver = findDriver(mongoClient, migrationConfiguration.getDatabaseName(configuration));
        var runner = MongockStandalone.builder()
                .setDriver(driver)
                .addMigrationScanPackage(migrationConfiguration.getMigrationPackage(configuration))
                .buildRunner();

        run(namespace, runner);
    }

    protected abstract void run(Namespace namespace, MongockRunner mongock);

    // TODO: More of a question, do we want to allow through config to pass in an explicit driver classname instead of relying on auto discovery?
    private ConnectionDriver findDriver(MongoClient client, String databaseName) {
        // TODO: When kiwi updates to spring data 3, then change this class to the v3 Spring data mongo driver
        var springDataDriverClass = findDriverClass("io.mongock.driver.mongodb.springdata.v2.SpringDataMongoV2Driver");

        if (springDataDriverClass.isPresent()) {
            return createSpringDataDriver(springDataDriverClass.get(), client, databaseName);
        }

        var syncDriverClass = findDriverClass("io.mongock.driver.mongodb.v3.driver.MongoCore3Driver")
                .or(() -> findDriverClass("io.mongock.driver.mongodb.sync.v4.driver.MongoSync4Driver"))
                .orElseThrow(() -> new IllegalStateException("Unable to find a valid Mongo driver for Mongock"));

        return createSyncDriver(syncDriverClass, client, databaseName);
    }

    private Optional<Class<?>> findDriverClass(String className) {
        try {
            var clazz = Class.forName(className);
            LOG.info("Found Mongock driver {}", className);
            return Optional.of(clazz);
        } catch (ClassNotFoundException e) {
            LOG.debug("Unable to find class {}", className, e);
            return Optional.empty();
        }
    }

    private ConnectionDriver createSyncDriver(Class<?> driverClass, MongoClient client, String databaseName) {
        var creationMethod = findMethod(driverClass, "withDefaultLock", MongoClient.class, String.class);
        return invokeExpectingReturn(creationMethod, null, ConnectionDriver.class, client, databaseName);
    }

    private ConnectionDriver createSpringDataDriver(Class<?> driverClass, MongoClient client, String databaseName) {
        Class<?> mongoTemplateClass;
        Object template;
        try {
            mongoTemplateClass = Class.forName("org.springframework.data.mongodb.core.MongoTemplate");
            template = mongoTemplateClass.getDeclaredConstructor(MongoClient.class, String.class).newInstance(client, databaseName);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to find or create MongoTemplate class which is required for the Spring Data Mongock drivers", e);
        }

        var creationMethod = findMethod(driverClass, "withDefaultLock", mongoTemplateClass);
        return invokeExpectingReturn(creationMethod, null, ConnectionDriver.class, template);
    }
}
