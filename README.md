### Dropwizard Mongo Migrations

[![Build](https://github.com/kiwiproject/dropwizard-mongo-migrations/workflows/build/badge.svg)](https://github.com/kiwiproject/dropwizard-mongo-migrations/actions?query=workflow%3Abuild)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kiwiproject_dropwizard-mongo-migrations&metric=alert_status)](https://sonarcloud.io/dashboard?id=kiwiproject_dropwizard-mongo-migrations)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kiwiproject_dropwizard-mongo-migrations&metric=coverage)](https://sonarcloud.io/dashboard?id=kiwiproject_dropwizard-mongo-migrations)
[![CodeQL](https://github.com/kiwiproject/dropwizard-mongo-migrations/actions/workflows/codeql.yml/badge.svg)](https://github.com/kiwiproject/dropwizard-mongo-migrations/actions/workflows/codeql.yml)
[![javadoc](https://javadoc.io/badge2/org.kiwiproject/dropwizard-mongo-migrations/javadoc.svg)](https://javadoc.io/doc/org.kiwiproject/dropwizard-mongo-migrations)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/org.kiwiproject/dropwizard-mongo-migrations)](https://central.sonatype.com/artifact/org.kiwiproject/dropwizard-mongo-migrations/)

Dropwizard Mongo Migrations is a simple library to assist in migrating Mongo databases in
[Dropwizard](https://www.dropwizard.io) applications. This library provides a set of Dropwizard commands similar to
Dropwizard's migration library for RDBMS systems.

The migrations are performed by the library [Mongock](https://www.mongock.io). This bundle sets up the configuration to
use the Mongock migration framework as a
Dropwizard [command](https://www.dropwizard.io/en/latest/manual/core.html#commands).

### Using dropwizard-mongo-migrations

To use this library, you need to add a `MongoMigrationsBundle`. Here is an example of adding a `MongoMigrationsBundle`
to a Dropwizard application.

First, provide necessary properties in your Configuration class. For example:

```java
// imports...

// Note this assumes Lombok to generate getter and setter methods
@Getter
@Setter
public class AppConfiguration extends Configuration {
    private String migrationPackage;
    private String mongoUri;
    private String mongoDbName;
}
```

Next, create a new `MongoMigrationsBundle` in the `initialize` of your `Application` class and add the bundle to
the `Bootstrap` object.

```java
// imports...

public class App extends Application<AppConfiguration> {

    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
        var migrationsBundle = new MongoMigrationsBundle<AppConfiguration>() {
            @Override
            public String getMigrationPackage(AppConfiguration config) {
                return config.getMigrationPackage();
            }

            @Override
            public String getMongoUri(AppConfiguration config) {
                return config.getMongoUri();
            }

            @Override
            public String getDatabaseName(AppConfiguration config) {
                return config.getMongoDbName();
            }

            @Override
            public ConnectionDriver getConnectionDriver(AppConfiguration config) {
                var mongoClient = MongoClients.create(getMongoUri(config));
                var mongoTemplate = new MongoTemplate(mongoClient, getDatabaseName(config));
                return SpringDataMongoV3Driver.withDefaultLock(mongoTemplate);
            }
        };

        bootstrap.addBundle(migrationsBundle);
    }

    @Override
    public void run(AppConfiguration configuration, Environment environment) {
        // code to set up your application's resources, etc. 
    }
}
```

Once this setup is complete, add Mongock migrations to the migration package that you provided to the bundle. You can
then run `db migrate` to run pending migrations.
