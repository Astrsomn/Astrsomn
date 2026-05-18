package com.astrsomn.api.runtime.common.mybatis;

import java.util.Collection;
import java.util.List;


public interface AstrsomnMybatisContributor {


    default Collection<String> getMapperScanPackages() {
        return List.of();
    }


    default Collection<String> getTypeAliasesPackages() {
        return List.of();
    }


    default Collection<String> getTenantTables() {
        return List.of();
    }
}
