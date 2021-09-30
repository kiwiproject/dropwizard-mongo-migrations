package org.kiwiproject.migrations;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kiwiproject.test.junit.jupiter.MongoServerExtension;

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
}

