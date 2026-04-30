package com.astrsomn.starter.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import com.astrsomn.starter.context.EnvRuntime;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class EnvCodeTenantHandler implements TenantLineHandler {


    @Resource
    private  AstrsomnProperties properties;

    /**
     * 需拼接 ENV_CODE 条件的表（小写，与 MP 传入表名归一后一致）。
     * <p>未在此列出的表将<b>不</b>做租户过滤（历史原因曾漏配会导致切换环境仍查到其它环境数据）。
     */
    private static final List<String> PRIVATE_TABLES = Arrays.asList(
            // ============ Core 模块 ============
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
            "ai_vec_doc",
            "ai_vec_driver",
            "ai_vec_segment",
            "ai_vec_source",
            "ai_vec_store",
            "system_config",
            "system_env",
            "system_extension",
            "system_message",
            "system_user",
            "astrsomn_job_log",
            // ============ Workflow 模块 ============
            "ast_flow_biz_idempotent",
            "ast_flow_definition",
            "ast_flow_deployment",
            "ast_flow_human_task",
            "ast_flow_instance",
            "ast_flow_instance_event",
            "ast_flow_msg_outbox",
            "ast_flow_node_config",
            "ast_flow_node_history",
            "ast_flow_timer_job"
    );

    @Override
    public Expression getTenantId() {
        String code = EnvRuntime.resolveEffectiveEnvCode(properties);
        return new StringValue(code);
    }

    @Override
    public String getTenantIdColumn() {
        // 数据库对应的字段名
        return "ENV_CODE";
    }

    @Override
    public boolean ignoreTable(String tableName) {
        // MP 传入的表名可能包含引号或大小写不一，统一转小写处理
        String cleanTableName = tableName.toLowerCase().replace("`", "");

        // 如果表名在私有表清单中，则【不忽略】（即：执行过滤）
        // 否则【忽略】（即：不拼接环境隔离条件）
        return !PRIVATE_TABLES.contains(cleanTableName);
    }

    /**
     * 方便日志打印输出
     */
    public String getPrivateTables() {
        return String.join(", ", PRIVATE_TABLES);
    }
}
