package org.kiwiproject.migrations.mongo;

import io.dropwizard.Configuration;
import io.mongock.runner.core.executor.MongockRunner;
import net.sourceforge.argparse4j.inf.Namespace;

public class DbMigrateCommand<T extends Configuration> extends AbstractMongockCommand<T> {

    public DbMigrateCommand(MongoMigrationConfiguration<T> migrationConfiguration, Class<T> configurationClass) {
        super("migrate", "Apply all pending change sets.", migrationConfiguration, configurationClass);
    }

    @Override
    protected void run(Namespace namespace, MongockRunner runner) {
        runner.execute();
    }
}
