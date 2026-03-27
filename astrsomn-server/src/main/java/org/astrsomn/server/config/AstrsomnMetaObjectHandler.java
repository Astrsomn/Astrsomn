package org.astrsomn.server.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.astrsomn.server.util.UserContext;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充：创建/更新时间、创建/更新人、环境编码（INSERT 时）。
 * <p>
 * 需在 {@link org.astrsomn.core.common.base.BaseEntity} 上配置 {@code @TableField(fill = ...)}，
 * 且在 {@link org.astrsomn.starter.config.MybatisPlusConfig} 的 GlobalConfig 中注册本 Handler。
 */
@Component
public class AstrsomnMetaObjectHandler implements MetaObjectHandler {

    private final AstrsomnProperties astrsomnProperties;

    public AstrsomnMetaObjectHandler(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        String user = resolveUser();
        this.strictInsertFill(metaObject, "createUser", String.class, user);
        this.strictInsertFill(metaObject, "updateUser", String.class, user);
        String env = resolveEnv();
        this.strictInsertFill(metaObject, "envCode", String.class, env);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
        String user = resolveUser();
        this.strictUpdateFill(metaObject, "updateUser", String.class, user);
    }

    private String resolveUser() {
        String u = UserContext.getUsername();
        if (StringUtils.isNotBlank(u)) {
            return u.trim();
        }
        Long id = UserContext.getUserId();
        if (id != null) {
            return String.valueOf(id);
        }
        return "system";
    }

    private String resolveEnv() {
        String env = StringUtils.trimToNull(astrsomnProperties.getEnvCode());
        return env != null ? env : "default";
    }
}
