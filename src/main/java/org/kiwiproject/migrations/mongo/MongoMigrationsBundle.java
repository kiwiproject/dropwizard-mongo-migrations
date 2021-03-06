package org.kiwiproject.migrations.mongo;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;

public abstract class MongoMigrationsBundle<T extends Configuration> implements ConfiguredBundle<T>, MongoMigrationConfiguration<T> {

    @SuppressWarnings("unchecked")
    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
        final Class<T> klass = (Class<T>) bootstrap.getApplication().getConfigurationClass();
        bootstrap.addCommand(new DbCommand<>(name(), this, klass));
    }

    public String name() {
        return getCommandName();
    }

}
