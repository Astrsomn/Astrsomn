package com.astrsomn.server.config;

import com.astrsomn.api.runtime.common.constant.SystemUserEnum.AdminEnum;
import com.astrsomn.api.runtime.common.constant.SystemUserEnum.UserRoleEnum;
import com.astrsomn.api.runtime.common.entity.SystemEnvEntity;
import com.astrsomn.api.runtime.common.entity.SystemUserEntity;
import com.astrsomn.common.base.BaseEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.mapper.AstSystemEnvMapper;
import com.astrsomn.starter.runtime.mapper.AstSystemUserMapper;
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

/**
 * 首次部署时初始化「默认运行环境」与「默认管理员」。
 * <p>
 * <b>环境与用户的关系</b>：{@link BaseEntity#envCode} 表示该记录所属运行环境；
 * 系统用户同样带有 {@code envCode}，表示该账号归属哪个环境（租户/业务域边界可按此字段扩展）。
 * <p>
 * <b>管理员可视范围（设计说明，非本类强制实现）</b>：
 * <ul>
 *   <li>若「一个用户 ≈ 一个业务系统」，则普通用户只应访问本 {@code envCode} 下的数据（列表查询带 envCode 条件，已有提示词等接口如此）。</li>
 *   <li>管理员是否跨环境：二选一或组合——(1) <b>环境内管理员</b>：{@code ADMIN_FLAG=Y} 且仅可管理同 {@code envCode} 的数据（与业务用户一致，仅权限更多）；
 *   (2) <b>平台超级管理员</b>：需单独标识（如角色表、独立账号或 {@code envCode} 为空表示全局），可在网关/拦截器里放行多环境查询。</li>
 *   <li>当前实现：默认管理员写入的 {@code envCode} 与 {@code astrsomn.env-code} 一致，与「本实例部署环境」对齐；后续若要多环境运维，请增加显式角色模型再改查询范围。</li>
 * </ul>
 */
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
    /**
     * 写入 SYSTEM_ENV 时的展示名称
     */
    @Value("${astrsomn.default-env.name:默认环境}")
    private String defaultEnvName;
    /**
     * 写入 SYSTEM_ENV 时的说明
     */
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

    /**
     * 延迟初始化，等待数据库表创建完成
     */
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

    /**
     * 执行初始化
     */
    private void doInitialize() {
        initializeDefaultEnv();
        initializeDefaultAdmin();
        validateUsername();
    }

    /**
     * 校验配置的 username 是否存在于数据库中
     */
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

    /**
     * 初始化默认环境
     * 若不存在与当前 {@code astrsomn.env-code} 对应的 {@link SystemEnvEntity#envKey}，则插入一条
     */
    private void initializeDefaultEnv() {
        if (isEnvExists(envCode)) {
            log.info("默认环境已存在 - envKey={}", envCode);
            return;
        }

        SystemEnvEntity envEntity = createDefaultEnvEntity();
        int rows = systemEnvMapper.insert(envEntity);
        log.info("初始化默认环境完成 - envKey={}, envName={}, 影响行数={}", envCode, defaultEnvName, rows);
    }

    /**
     * 检查环境是否存在
     */
    private boolean isEnvExists(String envKey) {
        SystemEnvEntity existingEnv = systemEnvMapper.selectOne(
                new LambdaQueryWrapper<SystemEnvEntity>()
                        .eq(SystemEnvEntity::getEnvKey, envKey)
                        .last("LIMIT 1")
        );
        return existingEnv != null;
    }

    /**
     * 创建默认环境实体
     */
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

    /**
     * 初始化默认管理员
     */
    private void initializeDefaultAdmin() {
        if (isAdminExists(defaultAdminUsername)) {
            log.info("默认管理员已存在 - username={}", defaultAdminUsername);
            return;
        }

        SystemUserEntity adminEntity = createDefaultAdminEntity();
        int rows = systemUserMapper.insert(adminEntity);
        log.info("初始化默认管理员完成 - username={}, envCode={}, 影响行数={}", defaultAdminUsername, envCode, rows);
    }

    /**
     * 检查管理员是否存在
     */
    private boolean isAdminExists(String username) {
        SystemUserEntity existingAdmin = systemUserMapper.selectOne(
                new LambdaQueryWrapper<SystemUserEntity>()
                        .eq(SystemUserEntity::getUsername, username)
        );
        return existingAdmin != null;
    }

    /**
     * 创建默认管理员实体
     */
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
