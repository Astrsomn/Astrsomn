package org.astrsomn.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "astrsomn")
public class AstrsomnProperties {

    /**
     * 当前部署环境的代码。
     * 用于多租户环境隔离（如：dev, test, prod），对应的数据库字段为 ENV_CODE。
     */
    private String envCode;

    /**
     * 逻辑引用：各环境在 application-*.yml 中配置「实际业务 key」，Java 只通过占位读取，
     * 避免 dev 配好的 key 在 uat 发布时还要改代码。
     * <p>
     * 示例：
     * <pre>
     * astrsomn:
     *   refs:
     *     default-agent-key: agt_support_bot_u10001
     *     default-model-key: mdl_openai_gpt_4o_u10001
     * </pre>
     */
    private Refs refs = new Refs();

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
         * 数据库类型。可选值：mysql, oracle。
         */
        private String databaseType = "mysql";

        /**
         * 数据库主机地址，默认为 localhost。
         */
        private String host = "localhost";

        /**
         * 数据库端口。MySQL 默认为 3306，Oracle 默认为 1521。
         */
        private Integer port;

        /**
         * 数据库名称（SID 或 Service Name）。
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
         * 完整 JDBC URL。如果配置此项，host/port/databaseName 将被忽略。
         */
        private String url;

        /**
         * Oracle 专用的 Schema 名称。
         */
        private String schema;

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
    }

    @Data
    public static class Refs {

        /**
         * {@code @Astro} 未写 agentKey 时使用；值在各环境配置中维护。
         */
        private String defaultAgentKey;

        /**
         * 代码中需要「默认模型 key」占位时（如路由、测试）使用，勿在业务里写死具体 key 字面量。
         */
        private String defaultModelKey;

        private String defaultPromptKey;

        private String defaultToolKey;

        private String defaultMcpKey;
    }
}