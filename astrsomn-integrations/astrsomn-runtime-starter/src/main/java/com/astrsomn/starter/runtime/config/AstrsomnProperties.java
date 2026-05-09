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
 * <p>示例 — 数据源校验（默认校验失败仅 ERROR 日志，不中断启动）：
 * <pre>{@code
 * astrsomn:
 *   enabled: true
 *   data-base:
 *     database-type: h2
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
     * 数据库连接配置。
     */
    private DataBase dataBase = new DataBase();

    @Data
    public static class MybatisPlus {

        /**
         * 额外的Mapper XML文件位置。
         * 支持多个路径，用逗号分隔。
         * 会自动与starter默认的mapper路径合并。
         * 
         * 示例：classpath:my-mapper/*.xml
         */
        private String additionalMapperLocations;

        /**
         * 额外的Entity类包路径（类型别名包）。
         * 支持多个包路径，用逗号分隔。
         * 会自动与starter默认的entity包合并。
         * 
         * 示例：com.mycompany.entity
         */
        private String additionalTypeAliasesPackage;
    }

    @Data
    public static class DataBase {

        /**
         * 数据库类型。可选值：mysql, h2。
         */
        private String databaseType = "mysql";

        /**
         * 数据库主机地址，默认为 localhost。
         */
        private String host = "localhost";

        /**
         * 数据库端口。MySQL 默认为 3306。
         */
        private Integer port;

        /**
         * 数据库名称或文件路径。
         */
        private String databaseName;

        /**
         * 数据库用户名。
         */
        private String username;

        /**
         * 数据库密码。
         */
        private String password;

        /**
         * 驱动类名。如果不指定，将根据 databaseType 自动选择默认驱动。
         */
        private String driver;

        /**
         * 完整 JDBC URL。如果配置此项，其他连接参数将被忽略。
         */
        private String url;

        /**
         * 是否启用 SSL 连接，默认为 false。
         */
        private Boolean useSsl = false;

        /**
         * 字符集编码，默认为 utf8。
         */
        private String charset = "utf8";

        /**
         * 时区配置，默认为 Asia/Shanghai。
         */
        private String timezone = "Asia/Shanghai";

        /**
         * 连接超时时间（毫秒），默认 30000。
         */
        private Integer connectionTimeout = 30000;

        /**
         * 连接池最大连接数，默认 10。
         */
        private Integer maximumPoolSize = 10;

        /**
         * 连接池最小空闲连接数，默认 5。
         */
        private Integer minimumIdle = 5;

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
