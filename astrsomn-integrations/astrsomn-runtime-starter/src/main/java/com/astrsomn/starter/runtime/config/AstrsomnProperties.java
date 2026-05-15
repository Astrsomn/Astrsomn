package com.astrsomn.starter.runtime.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 绑定前缀 {@code astrsomn}，由 {@link AstrsomnPropertiesAutoConfiguration#astrsomnProperties()} 注册为 Bean。
 *
 * <p>示例 — 关闭 starter 自带数据源与 MyBatis（由宿主自行提供 Bean）：
 * <pre>{@code
 * astrsomn:
 *   enabled: false
 * }</pre>
 *
 * <p>连接配置使用独立 Bean {@link com.astrsomn.starter.runtime.config.datasource.AstrsomnDatasourceProperties}（前缀 {@code astrsomn.datasource}）。
 * <pre>{@code
 * astrsomn:
 *   enabled: true
 *   datasource:
 *     url: jdbc:h2:file:./data/db;MODE=MySQL;DATABASE_TO_UPPER=FALSE
 *     username: sa
 *     password:
 *     driver-class-name: org.h2.Driver
 *     hikari:
 *       connection-timeout: 30000
 *       maximum-pool-size: 10
 *       minimum-idle: 5
 *   data-base:
 *     validation:
 *       enabled: true
 *       fail-fast: false
 *       required-tables:
 *         - SYS_ENV
 * }</pre>
 */
@Data
public class AstrsomnProperties {

    /**
     * 是否启用 runtime-starter 的数据源与 MyBatis 自动配置。
     * 为 {@code false} 时不加载 {@link AstrsomnAutoConfiguration}；宿主需自行提供 {@link javax.sql.DataSource} 等。
     */
    private Boolean enabled = true;

    /**
     * 当前部署环境的代码。
     * 用于多租户环境隔离（如：dev, test, prod），对应的数据库字段为 ENV_CODE。
     */
    private String envCode;

    /**
     * 默认用户名，用于 BaseEntity 的 createUser 和 updateUser 字段。
     * 当 UserContext 中获取不到用户信息时，使用此值作为默认值。
     * <p>
     * 配置此值时，必须确保该用户已存在于 SYS_USER 表中。
     */
    private String username;

    /**
     * 管理员用户名列表，用逗号分隔。
     * 这些用户具有环境切换权限，可以通过请求头切换工作空间。
     * <p>
     * 示例：admin,superadmin
     */
    private String adminUsers;

    /**
     * 加密密钥，用于加密 API Key 和 Secret。
     */
    private String accountKey = "astrsomn-account-key";

    /**
     * MyBatis-Plus 配置。
     */
    private MybatisPlus mybatisPlus = new MybatisPlus();

    /**
     * Schema / 连通性校验等扩展。
     */
    private DataBase dataBase = new DataBase();

    @Data
    public static class MybatisPlus {

        /**
         * 额外的Mapper XML文件位置。
         * 支持多个路径，用逗号分隔。
         * 会自动与starter默认的mapper路径合并。
         * <p>
         * 示例：classpath:my-mapper/*.xml
         */
        private String additionalMapperLocations;

        /**
         * 额外的Entity类包路径（类型别名包）。
         * 支持多个包路径，用逗号分隔。
         * 会自动与starter默认的entity包合并。
         * <p>
         * 示例：com.mycompany.entity
         */
        private String additionalTypeAliasesPackage;
    }

    @Data
    public static class DataBase {

        /**
         * 启动时数据源 / Schema 就绪校验（在 {@link org.apache.ibatis.session.SqlSessionFactory} 构建完成之后执行）。
         */
        private Validation validation = new Validation();
    }

    @Data
    public static class Validation {

        /**
         * 是否执行启动校验。
         */
        private Boolean enabled = true;

        /**
         * 用于判断 Schema 已就绪的表名（建议与 DDL 大写一致）。
         * 为空则仅校验 JDBC 连通性（如 {@code SELECT 1}）。
         */
        private List<String> requiredTables = new ArrayList<>(List.of("SYS_ENV"));

        /**
         * 校验失败时是否抛出异常并阻止 Spring 上下文刷新。默认 {@code false}，仅输出 ERROR 日志。
         */
        private Boolean failFast = false;

        /**
         * 单次校验获取连接的超时时间（秒）。未设置则依赖连接池 / 驱动的默认行为。
         */
        private Integer connectionTimeoutSeconds;
    }
}
