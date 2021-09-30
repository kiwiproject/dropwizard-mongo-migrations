package org.kiwiproject.migrations;

import com.github.cloudyrock.standalone.StandaloneRunner;
import io.dropwizard.Configuration;
import net.sourceforge.argparse4j.inf.Namespace;

public class DbMigrateCommand<T extends Configuration> extends AbstractMongockCommand<T> {

    public DbMigrateCommand(MongoMigrationConfiguration<T> migrationConfiguration, Class<T> configurationClass) {
        super("migrate", "Apply all pending change sets.", migrationConfiguration, configurationClass);
    }

    @Override
    protected void run(Namespace namespace, StandaloneRunner runner) {
        runner.execute();
    }
}
