package com.astrsomn.starter.runtime.vector.config;

import com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * vector-starter 的 MyBatis 配置贡献。
 */
@Component
public class VectorMybatisContributor implements AstrsomnMybatisContributor {

    @Override
    public Collection<String> getMapperScanPackages() {
        return List.of("com.astrsomn.starter.runtime.vector.mapper");
    }

    @Override
    public Collection<String> getTypeAliasesPackages() {
        return List.of("com.astrsomn.api.vector.entity");
    }

    @Override
    public Collection<String> getTenantTables() {
        return List.of(
                "ai_vec_doc",
                "ai_vec_driver",
                "ai_vec_segment",
                "ai_vec_source",
                "ai_vec_store"
        );
    }
}
