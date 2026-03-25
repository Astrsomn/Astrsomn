package org.astrsomn.server.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemUserEnum.AdminEnum;
import org.astrsomn.core.common.entity.SystemUserEntity;
import org.astrsomn.core.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 初始化默认管理员账号，确保首次部署即可登录。
 */
@Slf4j
@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    @Value("${astrsomn.default-admin.username:admin}")
    private String username;

    @Value("${astrsomn.default-admin.password:admin}")
    private String password;

    @Value("${astrsomn.default-admin.email:}")
    private String email;

    @Value("${astrsomn.env-code:pro}")
    private String envCode;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final SystemUserMapper systemUserMapper;

    public DefaultAdminInitializer(SystemUserMapper systemUserMapper) {
        this.systemUserMapper = systemUserMapper;
    }

    @Override
    public void run(String... args) {
        try {
            SystemUserEntity exist = systemUserMapper.selectOne(
                    new LambdaQueryWrapper<SystemUserEntity>()
                            .eq(SystemUserEntity::getUsername, username)
            );

            if (exist != null) {
                log.info("默认管理员已存在 - username={}", username);
                return;
            }

            SystemUserEntity entity = new SystemUserEntity();
            entity.setUsername(username);
            entity.setPassword(passwordEncoder.encode(password));
            entity.setEmail(email);
            entity.setAdminFlag(AdminEnum.YES.getCode());
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setEnvCode(envCode);

            int rows = systemUserMapper.insert(entity);
            log.info("初始化默认管理员完成 - username={}, rows={}", username, rows);
        } catch (Exception e) {
            // 初始化失败不应阻断应用启动
            log.error("初始化默认管理员失败: {}", e.getMessage(), e);
        }
    }
}

