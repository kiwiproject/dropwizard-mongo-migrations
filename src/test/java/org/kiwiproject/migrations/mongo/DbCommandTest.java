package org.kiwiproject.migrations.mongo;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kiwiproject.test.junit.jupiter.MongoServerExtension;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

class DbCommandTest {

    @RegisterExtension
    static final MongoServerExtension MONGO_SERVER_EXTENSION = new MongoServerExtension();

    private final DbCommand<TestMigrationConfiguration> dbCommand = new DbCommand<>("db",
            new TestMongoMigrationConfiguration(MONGO_SERVER_EXTENSION.getConnectionString(), MONGO_SERVER_EXTENSION.getTestDatabaseName()),
            TestMigrationConfiguration.class);

    @Test
    void testRunSubCommand() {

        dbCommand.run(null, new Namespace(Map.of("subcommand", "migrate")), new TestMigrationConfiguration());

        var uri = new MongoClientURI(MONGO_SERVER_EXTENSION.getConnectionString());
        var client = new MongoClient(uri);

        var db = client.getDatabase(MONGO_SERVER_EXTENSION.getTestDatabaseName());

        assertThat(db.getCollection("myCollection").countDocuments()).isEqualTo(1);
    }

    @Test
    void testPrintHelp() throws Exception {
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
        assertThat(dbCommand.getConfigurationClass()).isEqualTo(TestMigrationConfiguration.class);
    }
}

