package com.astrsomn.starter.config;

import com.astrsomn.commn.base.BaseEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.context.EnvRuntime;
import com.astrsomn.starter.context.UserContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充：创建/更新时间、创建/更新人、环境编码（INSERT 时）。
 * <p>
 * 通过反射方式实现，不需要在 {@link BaseEntity} 上配置 {@code @TableField(fill = ...)} 注解，
 * 保持 BaseEntity 的纯净性。
 */
@Slf4j
@Component
public class AstrsomnMetaObjectHandler implements MetaObjectHandler {

    private final AstrsomnProperties astrsomnProperties;

    public AstrsomnMetaObjectHandler(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseEntity) {
            BaseEntity<?> entity = (BaseEntity<?>) originalObject;
            LocalDateTime now = LocalDateTime.now();
            String user = resolveUser();
            String env = resolveEnv();
            log.info("====>  mybatis自动注入：{} , {}, {}", now, user, env);
            setIfNull(entity, "createTime", now);
            setIfNull(entity, "updateTime", now);
            setIfNull(entity, "createUser", user);
            setIfNull(entity, "updateUser", user);
            setIfNull(entity, "envCode", env);
            setIfNull(entity, "deleted", false);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object originalObject = metaObject.getOriginalObject();
        if (originalObject instanceof BaseEntity) {
            BaseEntity<?> entity = (BaseEntity<?>) originalObject;
            LocalDateTime now = LocalDateTime.now();
            String user = resolveUser();

            entity.setUpdateTime(now);
            entity.setUpdateUser(user);
        }
    }

    private <T> void setIfNull(Object target, String fieldName, T value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                Object currentValue = field.get(target);
                if (currentValue == null) {
                    field.set(target, value);
                }
            }
        } catch (Exception e) {
            log.debug("Failed to set field {}: {}", fieldName, e.getMessage());
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return findField(superClass, fieldName);
            }
            return null;
        }
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
        return astrsomnProperties.getUsername().trim();
    }

    private String resolveEnv() {
        return EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties);
    }
}
