package org.kiwiproject.migrations;

import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestMigrationConfiguration extends Configuration {

    private String migrationPackage;
    private String mongoUri;

}