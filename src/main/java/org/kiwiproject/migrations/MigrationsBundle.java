package org.kiwiproject.migrations;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;

public abstract class MigrationsBundle <T extends Configuration> implements ConfiguredBundle<T>, MongoMigrationConfiguration<T> {

    private static final String DEFAULT_NAME = "db";

    @SuppressWarnings("unchecked")
    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
        final Class<T> klass = (Class<T>) bootstrap.getApplication().getConfigurationClass();
        bootstrap.addCommand(new DbCommand<>(name(), this, klass));
    }

    public String name() {
        return DEFAULT_NAME;
    }

}
