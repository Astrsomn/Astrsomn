package com.astrsomn.server.config;

import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.system.mapper.AstSystemEnvMapper;
import com.astrsomn.starter.runtime.system.mapper.AstSystemUserMapper;
import com.astrsomn.system.constant.SystemUserEnum.AdminEnum;
import com.astrsomn.system.constant.SystemUserEnum.UserRoleEnum;
import com.astrsomn.system.entity.SystemEnvEntity;
import com.astrsomn.system.entity.SystemUserEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class DefaultAdminInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Value("${astrsomn.default-admin.username:admin}")
    private String defaultAdminUsername;
    @Value("${astrsomn.default-admin.password:admin}")
    private String defaultAdminPassword;
    @Value("${astrsomn.default-admin.email:}")
    private String defaultAdminEmail;
    @Value("${astrsomn.env-code:pro}")
    private String envCode;
    
    @Value("${astrsomn.default-env.name:默认环境}")
    private String defaultEnvName;
    
    @Value("${astrsomn.default-env.description:与 astrsomn.env-code 对应，应用首次启动时自动创建。}")
    private String defaultEnvDescription;
    private AstSystemUserMapper systemUserMapper;
    private AstSystemEnvMapper systemEnvMapper;
    private AstrsomnProperties astrsomnProperties;

    @Autowired(required = false)
    public void setSystemUserMapper(AstSystemUserMapper systemUserMapper) {
        this.systemUserMapper = systemUserMapper;
    }

    @Autowired(required = false)
    public void setSystemEnvMapper(AstSystemEnvMapper systemEnvMapper) {
        this.systemEnvMapper = systemEnvMapper;
    }

    @Autowired(required = false)
    public void setAstrsomnProperties(AstrsomnProperties astrsomnProperties) {
        this.astrsomnProperties = astrsomnProperties;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (systemUserMapper == null || systemEnvMapper == null) {
            log.warn("DefaultAdminInitializer: 必要依赖未注入，跳过初始化");
            return;
        }
        delayedInitialize();
    }

    
    private void delayedInitialize() {
        try {
            doInitialize();
        } catch (Exception e) {
            log.warn("默认环境/管理员初始化失败，将在延迟后重试 | 异常: {}", e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(2);
                doInitialize();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("默认环境/管理员初始化重试被中断");
            } catch (Exception e2) {
                log.error("默认环境/管理员初始化重试失败 | 异常: {}", e2.getMessage(), e2);
            }
        }
    }

    
    private void doInitialize() {
        initializeDefaultEnv();
        initializeDefaultAdmin();
        validateUsername();
    }

    
    private void validateUsername() {
        if (astrsomnProperties == null) {
            log.warn("DefaultAdminInitializer: AstrsomnProperties 未注入，跳过 username 校验");
            return;
        }

        String username = astrsomnProperties.getUsername();
        if (StringUtils.isBlank(username)) {
            String errorMsg = "Astrsomn configuration error: astrsomn.username must be configured";
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        username = username.trim();
        LambdaQueryWrapper<SystemUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemUserEntity::getUsername, username);

        SystemUserEntity user = systemUserMapper.selectOne(queryWrapper);

        if (user == null) {
            String errorMsg = String.format(
                    "Astrsomn configuration error: username '%s' does not exist in SYS_USER table.",
                    username
            );
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        log.info("Astrsomn configuration validation passed: username '{}' exists in database.", username);
    }

    
    private void initializeDefaultEnv() {
        if (isEnvExists(envCode)) {
            log.info("默认环境已存在 - envKey={}", envCode);
            return;
        }

        SystemEnvEntity envEntity = createDefaultEnvEntity();
        int rows = systemEnvMapper.insert(envEntity);
        log.info("初始化默认环境完成 - envKey={}, envName={}, 影响行数={}", envCode, defaultEnvName, rows);
    }

    
    private boolean isEnvExists(String envKey) {
        SystemEnvEntity existingEnv = systemEnvMapper.selectOne(
                new LambdaQueryWrapper<SystemEnvEntity>()
                        .eq(SystemEnvEntity::getEnvKey, envKey)
                        .last("LIMIT 1")
        );
        return existingEnv != null;
    }

    
    private SystemEnvEntity createDefaultEnvEntity() {
        SystemEnvEntity entity = new SystemEnvEntity();
        entity.setEnvKey(envCode);
        entity.setEnvName(defaultEnvName);
        entity.setDescription(defaultEnvDescription);
        entity.setEnvCode(envCode);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setDeleted(Boolean.FALSE);
        return entity;
    }

    
    private void initializeDefaultAdmin() {
        if (isAdminExists(defaultAdminUsername)) {
            log.info("默认管理员已存在 - username={}", defaultAdminUsername);
            return;
        }

        SystemUserEntity adminEntity = createDefaultAdminEntity();
        int rows = systemUserMapper.insert(adminEntity);
        log.info("初始化默认管理员完成 - username={}, envCode={}, 影响行数={}", defaultAdminUsername, envCode, rows);
    }

    
    private boolean isAdminExists(String username) {
        SystemUserEntity existingAdmin = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUserEntity>()
                        .eq(SystemUserEntity::getUsername, username)
        );
        return existingAdmin != null;
    }

    
    private SystemUserEntity createDefaultAdminEntity() {
        SystemUserEntity entity = new SystemUserEntity();
        entity.setUsername(defaultAdminUsername);
        entity.setPassword(passwordEncoder.encode(defaultAdminPassword));
        entity.setEmail(defaultAdminEmail);
        entity.setUserRole(UserRoleEnum.SUPER_ADMIN.getCode());
        entity.setAdminFlag(AdminEnum.YES.getCode());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setEnvCode(envCode);
        return entity;
    }
}
