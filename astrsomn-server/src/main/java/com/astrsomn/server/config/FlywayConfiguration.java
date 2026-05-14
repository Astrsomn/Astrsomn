package com.astrsomn.server.config;

import com.astrsomn.starter.runtime.schema.SchemaInitializer;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FlywayProperties.class)
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
public class FlywayConfiguration {

    @Bean
    public SchemaInitializer flywaySchemaInitializer(FlywayProperties properties) {
        return (DataSource dataSource) -> {
            var config = Flyway.configure()
                    .dataSource(dataSource)
                    .encoding(properties.getEncoding() != null ? properties.getEncoding() : StandardCharsets.UTF_8)
                    .baselineOnMigrate(properties.isBaselineOnMigrate());

            if (properties.getLocations() != null && !properties.getLocations().isEmpty()) {
                config.locations(toArray(properties.getLocations()));
            }
            if (!isBlank(properties.getBaselineVersion())) {
                config.baselineVersion(MigrationVersion.fromVersion(properties.getBaselineVersion()));
            }
            if (!isBlank(properties.getTable())) {
                config.table(properties.getTable());
            }

            config.load().migrate();

            log.info("[Astrsomn] Flyway migration finished");
        };
    }

    private static String[] toArray(List<String> locations) {
        if (locations == null || locations.isEmpty()) return new String[0];
        return locations.toArray(new String[0]);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

