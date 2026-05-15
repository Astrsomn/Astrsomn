package com.astrsomn.starter.runtime.system.config;

import com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * system-starter 的 MyBatis 配置贡献。
 */
@Component
public class SystemMybatisContributor implements AstrsomnMybatisContributor {

    @Override
    public Collection<String> getMapperScanPackages() {
        return List.of("com.astrsomn.starter.runtime.system.mapper");
    }

    @Override
    public Collection<String> getTypeAliasesPackages() {
        return List.of("com.astrsomn.system.entity");
    }

    @Override
    public Collection<String> getTenantTables() {
        return List.of(
                "system_message"
        );
    }
}
