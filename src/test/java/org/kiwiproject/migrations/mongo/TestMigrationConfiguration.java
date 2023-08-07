package org.kiwiproject.migrations.mongo;

import io.dropwizard.core.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestMigrationConfiguration extends Configuration {

    private String migrationPackage;
    private String mongoUri;

}
