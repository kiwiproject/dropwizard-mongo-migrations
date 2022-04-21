package org.kiwiproject.migrations.mongo;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClients;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

class DbCommandTest {

    @RegisterExtension
    static final MongoServerExtension MONGO_SERVER_EXTENSION = new MongoServerExtension();

    private String mongoConnectionString;
    private String mongoDatabaseName;

    @BeforeEach
    void setUp() {
        mongoConnectionString = MONGO_SERVER_EXTENSION.getConnectionString();
        mongoDatabaseName = MONGO_SERVER_EXTENSION.getTestDatabaseName();
    }

    @Test
    void testRunSubCommandWithMongoDatabase() {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(mongoConnectionString,
                        mongoDatabaseName, "org.kiwiproject.migrations.mongo.samples.mongodatabase"),
                TestMigrationConfiguration.class);

        dbCommand.run(null, new Namespace(Map.of("subcommand", "migrate")), new TestMigrationConfiguration());

        var client = MongoClients.create(mongoConnectionString);

        var db = client.getDatabase(mongoDatabaseName);

        assertThat(db.getCollection("myCollection").countDocuments()).isEqualTo(1);
    }

    @Test
    void testRunSubCommandWithMongoTemplate() {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(mongoConnectionString,
                        mongoDatabaseName, "org.kiwiproject.migrations.mongo.samples.mongotemplate"),
                TestMigrationConfiguration.class);

        dbCommand.run(null, new Namespace(Map.of("subcommand", "migrate")), new TestMigrationConfiguration());

        var client = MongoClients.create(mongoConnectionString);

        var db = client.getDatabase(mongoDatabaseName);

        assertThat(db.getCollection("myTemplateCollection").countDocuments()).isEqualTo(1);
    }

    @Test
    void testPrintHelp() throws Exception {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(mongoConnectionString,
                        mongoDatabaseName, "org.kiwiproject.migrations.mongo.samples.mongodatabase"),
                TestMigrationConfiguration.class);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        createSubparser(dbCommand).printHelp(new PrintWriter(new OutputStreamWriter(baos, UTF_8), true));
        assertThat(baos.toString(UTF_8.name())).isEqualTo(String.format(
                "usage: db db [-h] {migrate} ...%n" +
                        "%n" +
                        "Run database migration tasks%n" +
                        "%n" +
                        "positional arguments:%n" +
                        "  {migrate}%n" +
                        "%n" +
                        "named arguments:%n" +
                        "  -h, --help             show this help message and exit%n"));
    }

    protected static Subparser createSubparser(AbstractMongockCommand<?> command) {
        final Subparser subparser = ArgumentParsers.newFor("db")
                .terminalWidthDetection(false)
                .build()
                .addSubparsers()
                .addParser(command.getName())
                .description(command.getDescription());
        command.configure(subparser);
        return subparser;
    }

    @Test
    void shouldReturnConfigurationClass() {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(mongoConnectionString,
                        mongoDatabaseName, "org.kiwiproject.migrations.mongo.samples.mongodatabase"),
                TestMigrationConfiguration.class);

        assertThat(dbCommand.getConfigurationClass()).isEqualTo(TestMigrationConfiguration.class);
    }
}

