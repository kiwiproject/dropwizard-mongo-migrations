package org.kiwiproject.migrations.mongo;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kiwiproject.migrations.mongo.MongoTestContainerHelpers.newMongoDBContainer;

import com.mongodb.client.MongoClients;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

@DisplayName("DbCommand")
@Testcontainers(disabledWithoutDocker = true)
class DbCommandTest {

    @Container
    static final MongoDBContainer MONGODB = newMongoDBContainer();

    private String mongoConnectionString;
    private String mongoDatabaseName;

    @BeforeEach
    void setUp() {
        mongoConnectionString = MONGODB.getConnectionString();
        mongoDatabaseName = "test";
    }

    @Test
    void testRunSubCommandWithMongoDatabase() {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(
                        mongoConnectionString,
                        mongoDatabaseName,
                        "org.kiwiproject.migrations.mongo.samples.mongodatabase"),
                TestMigrationConfiguration.class);

        dbCommand.run(null, new Namespace(Map.of("subcommand", "migrate")), new TestMigrationConfiguration());

        try (var client = MongoClients.create(mongoConnectionString)) {
            var db = client.getDatabase(mongoDatabaseName);
            assertThat(db.getCollection("myCollection").countDocuments()).isEqualTo(1);
        }
    }

    @Test
    void testRunSubCommandWithMongoTemplate() {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(mongoConnectionString,
                        mongoDatabaseName,
                        "org.kiwiproject.migrations.mongo.samples.mongotemplate"),
                TestMigrationConfiguration.class);

        dbCommand.run(null, new Namespace(Map.of("subcommand", "migrate")), new TestMigrationConfiguration());

        try (var client = MongoClients.create(mongoConnectionString)) {
            var db = client.getDatabase(mongoDatabaseName);
            assertThat(db.getCollection("myTemplateCollection").countDocuments()).isEqualTo(1);
        }
    }

    @Test
    void testPrintHelp() {
        var dbCommand = new DbCommand<>("db",
                new TestMongoMigrationConfiguration(mongoConnectionString,
                        mongoDatabaseName,
                        "org.kiwiproject.migrations.mongo.samples.mongodatabase"),
                TestMigrationConfiguration.class);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        createSubparser(dbCommand).printHelp(new PrintWriter(new OutputStreamWriter(baos, UTF_8), true));
        assertThat(baos.toString(UTF_8)).isEqualTo(String.format(
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
                        mongoDatabaseName,
                        "org.kiwiproject.migrations.mongo.samples.mongodatabase"),
                TestMigrationConfiguration.class);

        assertThat(dbCommand.getConfigurationClass()).isEqualTo(TestMigrationConfiguration.class);
    }
}

