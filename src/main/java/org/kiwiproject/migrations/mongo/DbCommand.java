package org.kiwiproject.migrations.mongo;

import static java.util.Objects.requireNonNull;

import com.github.cloudyrock.standalone.StandaloneRunner;
import io.dropwizard.Configuration;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.SortedMap;
import java.util.TreeMap;

public class DbCommand<T extends Configuration> extends AbstractMongockCommand<T> {

    private static final String COMMAND_NAME_ATTR = "subcommand";
    private final SortedMap<String, AbstractMongockCommand<T>> subcommands;

    public DbCommand(String name, MongoMigrationConfiguration<T> migrationConfig, Class<T> configurationClass) {
        super(name, "Run database migration tasks", migrationConfig, configurationClass);

        this.subcommands = new TreeMap<>();
        addSubcommand(new DbMigrateCommand<>(migrationConfig, configurationClass));
    }

    private void addSubcommand(AbstractMongockCommand<T> subcommand) {
        subcommands.put(subcommand.getName(), subcommand);
    }

    protected void run(Namespace namespace, StandaloneRunner runner) {
        var subcommand = requireNonNull(subcommands.get(namespace.getString(COMMAND_NAME_ATTR)), "Unable to find the command");
        subcommand.run(namespace, runner);
    }
}
