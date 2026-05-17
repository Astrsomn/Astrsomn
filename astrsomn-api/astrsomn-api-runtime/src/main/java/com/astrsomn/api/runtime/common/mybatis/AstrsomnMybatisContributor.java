package com.astrsomn.api.runtime.common.mybatis;

import java.util.Collection;
import java.util.List;

/**
 * MyBatis 配置贡献 SPI。
 * <p>
 * 各 starter 模块实现此接口，向 runtime-starter 的基础设施注册自己的 mapper 包、entity 包和租户隔离表。
 * 这样新增 starter 时无需修改 runtime-starter 的代码。
 */
public interface AstrsomnMybatisContributor {

    /**
     * 要扫描的 mapper 包路径
     */
    default Collection<String> getMapperScanPackages() {
        return List.of();
    }

    /**
     * 要加入 typeAliases 的 entity 包路径
     */
    default Collection<String> getTypeAliasesPackages() {
        return List.of();
    }

    /**
     * 要加入租户隔离的表名（小写）
     */
    default Collection<String> getTenantTables() {
        return List.of();
    }
}
