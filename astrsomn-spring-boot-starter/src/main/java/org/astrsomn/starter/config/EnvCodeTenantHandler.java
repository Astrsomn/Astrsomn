package org.astrsomn.starter.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.astrsomn.starter.context.EnvRuntime;
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
            "ai_agent",
            "ai_model",
            "ai_prompt",
            "ai_prompt_config",
            "ai_tool",
            "ai_mcp",
            "ai_template",
            "ai_conversation",
            "astrsomn_job_log"
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
