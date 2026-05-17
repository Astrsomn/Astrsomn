package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.Set;


/**
 * 环境隔离租户处理器。
 * <p>
 * 表名集合由各 starter 模块通过 {@link com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor} 动态贡献，
 * 不再硬编码。
 */
@RequiredArgsConstructor
public class EnvCodeTenantHandler implements TenantLineHandler {

    /**
     * 需拼接 ENV_CODE 条件的表（小写，与 MP 传入表名归一后一致）。
     * 由 {@link MybatisPlusConfig} 从所有 {@link com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor} 收集后注入。
     */
    private final Set<String> privateTables;

    private final AstrsomnProperties properties;

    @Override
    public Expression getTenantId() {
        String code = EnvRuntime.resolveEffectiveEnvCode(properties);
        return new StringValue(code);
    }

    @Override
    public String getTenantIdColumn() {
        return "ENV_CODE";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        String cleanTableName = tableName.toLowerCase().replace("`", "");
        return !privateTables.contains(cleanTableName);
    }

    /**
     * 方便日志打印输出
     */
    public String getPrivateTables() {
        return String.join(", ", privateTables);
    }
}
