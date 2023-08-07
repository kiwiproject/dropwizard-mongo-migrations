package org.kiwiproject.migrations.mongo;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;

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
