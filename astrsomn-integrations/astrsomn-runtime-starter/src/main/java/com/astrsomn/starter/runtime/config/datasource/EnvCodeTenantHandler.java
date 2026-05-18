package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.context.EnvRuntime;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

import java.util.Set;



@RequiredArgsConstructor
public class EnvCodeTenantHandler implements TenantLineHandler {

    
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

    
    public String getPrivateTables() {
        return String.join(", ", privateTables);
    }
}
