package com.astrsomn.starter.runtime.config;

import com.astrsomn.common.base.BaseEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.astrsomn.starter.runtime.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Properties;

/**
 * MyBatis 审计字段拦截器。
 * <p>
 * 在 Executor.update() 执行前（SQL 生成之前）注入审计字段值，
 * 比 MetaObjectHandler 更底层，不依赖 MyBatis-Plus 的 Handler 注册机制。
 */
@Slf4j
@Component
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class AstrsomnAuditInterceptor implements Interceptor {

    private final AstrsomnProperties astrsomnProperties;

    public AstrsomnAuditInterceptor(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args.length > 1 && args[1] instanceof BaseEntity<?> entity) {
            MappedStatement ms = (MappedStatement) args[0];
            SqlCommandType sqlType = ms.getSqlCommandType();

            if (sqlType == SqlCommandType.INSERT) {
                LocalDateTime now = LocalDateTime.now();
                String user = resolveUser();
                String env = resolveEnv();

                entity.setCreateTime(now);
                entity.setUpdateTime(now);
                entity.setCreateUser(user);
                entity.setUpdateUser(user);
                entity.setEnvCode(env);
                entity.setDeleted(false);

                log.info("[AuditInterceptor] INSERT entityClass={}, createTime={}, createUser={}, envCode={}",
                        entity.getClass().getSimpleName(), now, user, env);
            } else if (sqlType == SqlCommandType.UPDATE) {
                LocalDateTime now = LocalDateTime.now();
                String user = resolveUser();

                entity.setUpdateTime(now);
                entity.setUpdateUser(user);

                log.info("[AuditInterceptor] UPDATE entityClass={}, updateTime={}, updateUser={}",
                        entity.getClass().getSimpleName(), now, user);
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
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
