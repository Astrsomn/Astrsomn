package com.astrsomn.starter.runtime.config;

import com.astrsomn.common.base.BaseEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 兜底审计字段填充拦截器（纯反射，不依赖 BaseEntity 的 MP 注解）。
 * <p>
 * 在执行 INSERT/UPDATE 前为 BaseEntity 自动补齐审计字段。
 */
@Slf4j
@RequiredArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class ReflectiveAuditAutoFillInterceptor implements Interceptor {

    private final AstrsomnProperties astrsomnProperties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object parameter = args[1];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        if (sqlCommandType == SqlCommandType.INSERT || sqlCommandType == SqlCommandType.UPDATE) {
            List<BaseEntity<?>> entities = new ArrayList<>();
            collectEntities(parameter, entities);
            if (!entities.isEmpty()) {
                String user = resolveUser();
                String env = resolveEnv();
                LocalDateTime now = LocalDateTime.now();
                for (BaseEntity<?> entity : entities) {
                    if (sqlCommandType == SqlCommandType.INSERT) {
                        setIfNull(entity, "createTime", now);
                        setIfNull(entity, "createUser", user);
                        setIfNull(entity, "envCode", env);
                        setIfNull(entity, "deleted", false);
                    }
                    setIfNull(entity, "updateTime", now);
                    setIfNull(entity, "updateUser", user);
                }
            }
        }

        return invocation.proceed();
    }

    private void collectEntities(Object parameter, List<BaseEntity<?>> out) {
        if (parameter == null) {
            return;
        }
        if (parameter instanceof BaseEntity<?> entity) {
            out.add(entity);
            return;
        }
        if (parameter instanceof Map<?, ?> map) {
            for (Object value : map.values()) {
                if (value instanceof BaseEntity<?> entity) {
                    out.add(entity);
                }
            }
        }
    }

    private <T> void setIfNull(Object target, String fieldName, T value) {
        try {
            Field field = findField(target.getClass(), fieldName);
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            if (field.get(target) == null) {
                field.set(target, value);
            }
        } catch (Exception e) {
            log.debug("Reflective audit fill failed for field {}: {}", fieldName, e.getMessage());
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
        String username = UserContext.getUsername();
        if (StringUtils.isNotBlank(username)) {
            return username.trim();
        }
        Long userId = UserContext.getUserId();
        if (userId != null) {
            return String.valueOf(userId);
        }
        return astrsomnProperties.getUsername().trim();
    }

    private String resolveEnv() {
        return EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties);
    }
}
