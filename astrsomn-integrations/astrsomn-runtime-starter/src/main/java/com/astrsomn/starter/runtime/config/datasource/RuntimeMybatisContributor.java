package com.astrsomn.starter.runtime.config.datasource;

import com.astrsomn.api.runtime.common.mybatis.AstrsomnMybatisContributor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


@Component
public class RuntimeMybatisContributor implements AstrsomnMybatisContributor {

    @Override
    public Collection<String> getMapperScanPackages() {
        return List.of("com.astrsomn.starter.runtime.mapper");
    }

    @Override
    public Collection<String> getTypeAliasesPackages() {
        return List.of("com.astrsomn.api.runtime.common.entity");
    }

    @Override
    public Collection<String> getTenantTables() {
        return List.of(
                "ai_account",
                "ai_agent",
                "ai_chat_message",
                "ai_chat_session",
                "ai_instance",
                "ai_mcp",
                "ai_model",
                "ai_prompt",
                "ai_prompt_config",
                "ai_sensitive_word",
                "ai_template",
                "ai_tool",
                "ai_trace_log",
                "astrsomn_job_log"
        );
    }
}
