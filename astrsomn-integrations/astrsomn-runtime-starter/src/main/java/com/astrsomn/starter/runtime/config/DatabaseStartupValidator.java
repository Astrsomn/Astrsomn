package com.astrsomn.starter.runtime.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 在 SqlSessionFactory（含可选 {@link com.astrsomn.starter.runtime.schema.SchemaInitializer}）就绪后校验数据源连通性及必选表。
 */
@Slf4j
@RequiredArgsConstructor
public class DatabaseStartupValidator implements InitializingBean {

    private final DataSource dataSource;
    private final AstrsomnDatasourceProperties datasourceProperties;
    private final AstrsomnProperties astrsomnProperties;
    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public void afterPropertiesSet() {
        Objects.requireNonNull(sqlSessionFactory, "sqlSessionFactory");
        AstrsomnProperties.Validation cfg =
                astrsomnProperties.getDataBase() != null && astrsomnProperties.getDataBase().getValidation() != null
                        ? astrsomnProperties.getDataBase().getValidation()
                        : new AstrsomnProperties.Validation();

        boolean failFast = Boolean.TRUE.equals(cfg.getFailFast());
        try {
            validate(cfg);
        } catch (Exception e) {
            logFailure(cfg, e);
            if (failFast) {
                throw new IllegalStateException("[Astrsomn] 数据源校验失败（fail-fast=true）", e);
            }
        }
    }

    private void validate(AstrsomnProperties.Validation cfg) throws Exception {
        Integer timeoutSec = cfg.getConnectionTimeoutSeconds();
        Runnable validationTask = () -> {
            try {
                runValidation(cfg);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        if (timeoutSec != null && timeoutSec > 0) {
            ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "astrsomn-db-validation");
                t.setDaemon(true);
                return t;
            });
            try {
                Future<?> future = executor.submit(validationTask);
                future.get(timeoutSec.longValue(), TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new SQLException("数据源校验超时（" + timeoutSec + "s）", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new SQLException("数据源校验被中断", e);
            } catch (ExecutionException e) {
                Throwable c = e.getCause();
                if (c instanceof SQLException se) {
                    throw se;
                }
                if (c instanceof RuntimeException re) {
                    if (re.getCause() instanceof SQLException se) {
                        throw se;
                    }
                }
                throw new SQLException("数据源校验失败", c != null ? c : e);
            } finally {
                executor.shutdownNow();
            }
        } else {
            runValidation(cfg);
        }
    }

    private void runValidation(AstrsomnProperties.Validation cfg) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(5)) {
                throw new SQLException("连接 isValid(5) 为 false");
            }
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT 1")) {
                if (!rs.next()) {
                    throw new SQLException("连通性检测无结果行");
                }
            }

            List<String> tables = cfg.getRequiredTables();
            if (tables == null || tables.isEmpty()) {
                log.info("[Astrsomn] 数据源校验通过（仅连通性，未配置 requiredTables）");
                return;
            }

            List<String> missing = new ArrayList<>();
            for (String raw : tables) {
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                String name = raw.trim();
                if (!tableExists(conn, name)) {
                    missing.add(name);
                }
            }
            if (!missing.isEmpty()) {
                throw new SQLException("缺少表（未迁移或未初始化）: " + missing);
            }
            log.info("[Astrsomn] 数据源校验通过（连通性 + 表存在）");
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        String catalog = conn.getCatalog();
        String schema = conn.getSchema();
        String[] schemaPatterns = buildSchemaPatterns(schema);
        String[] names = {tableName, tableName.toUpperCase(Locale.ROOT)};

        for (String schemaPattern : schemaPatterns) {
            for (String name : names) {
                try (ResultSet rs = md.getTables(catalog, schemaPattern, name, new String[]{"TABLE"})) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String[] buildSchemaPatterns(String schema) {
        if (schema == null || schema.isEmpty()) {
            return new String[]{null, "PUBLIC"};
        }
        return new String[]{schema, null, "PUBLIC"};
    }

    private void logFailure(AstrsomnProperties.Validation cfg, Exception e) {
        String jdbcUrl = datasourceProperties != null ? datasourceProperties.getUrl() : null;
        String hint = "请检查 astrsomn.datasource.url / 账号密码、网络、以及是否已执行数据库迁移（Flyway 等）。"
                + " 若缺表，请确认迁移已成功执行。";
        String urlInfo = sanitizeJdbcUrl(jdbcUrl);
        String dbLabel = describeJdbcUrl(jdbcUrl);
        log.error(
                "[Astrsomn] 数据源启动校验未通过（fail-fast={}，默认应用继续启动）。jdbc={} url={} | {}",
                Boolean.TRUE.equals(cfg.getFailFast()),
                dbLabel,
                urlInfo,
                hint,
                e
        );
    }

    private static String describeJdbcUrl(String jdbcUrl) {
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            return "(unknown)";
        }
        try {
            return JdbcUrlDbSupport.resolveMybatisDbType(jdbcUrl).getDb();
        } catch (IllegalArgumentException ex) {
            return "(unsupported)";
        }
    }

    private static String sanitizeJdbcUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "(empty)";
        }
        return url.replaceAll("(?i)(password|passwd)=([^;&]*)", "$1=***");
    }
}
