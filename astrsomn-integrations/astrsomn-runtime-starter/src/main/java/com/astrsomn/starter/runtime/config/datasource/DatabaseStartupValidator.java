package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


@Slf4j
public class DatabaseStartupValidator {

    private final DataSource dataSource;
    private final AstrsomnDatasourceProperties datasourceProperties;
    private final AstrsomnProperties astrsomnProperties;
    private final SqlSessionFactory sqlSessionFactory;

    public DatabaseStartupValidator(DataSource dataSource,
                                    AstrsomnDatasourceProperties datasourceProperties,
                                    AstrsomnProperties astrsomnProperties,
                                    SqlSessionFactory sqlSessionFactory) {
        this.dataSource = dataSource;
        this.datasourceProperties = datasourceProperties;
        this.astrsomnProperties = astrsomnProperties;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        AstrsomnProperties.Validation validation = astrsomnProperties.getDataBase().getValidation();
        if (validation == null || !Boolean.TRUE.equals(validation.getEnabled())) {
            return;
        }
        try (Connection conn = dataSource.getConnection()) {
            validateConnectivity(conn);
            validateRequiredTables(conn, validation.getRequiredTables());
        } catch (Exception e) {
            String msg = "Astrsomn database validation failed: " + e.getMessage();
            if (Boolean.TRUE.equals(validation.getFailFast())) {
                throw new IllegalStateException(msg, e);
            }
            log.error(msg, e);
        }
    }

    private void validateConnectivity(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next()) {
                log.info("[Astrsomn] datasource connectivity OK");
            }
        }
    }

    private void validateRequiredTables(Connection conn, List<String> requiredTables) {
        if (requiredTables == null || requiredTables.isEmpty()) {
            return;
        }
        for (String table : requiredTables) {
            try (ResultSet rs = conn.getMetaData().getTables(null, null, table, new String[]{"TABLE"})) {
                if (!rs.next()) {
                    log.warn("[Astrsomn] required table not found: {}", table);
                }
            } catch (SQLException e) {
                log.warn("[Astrsomn] failed to check table {}: {}", table, e.getMessage());
            }
        }
    }
}
