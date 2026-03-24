package org.astrsomn.starter.config;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Component
@RequiredArgsConstructor
public class EnvCodeTenantHandler implements TenantLineHandler {


    @Resource
    private  AstrsomnProperties properties;

    // 需要进行环境隔离的私有表白名单
    private static final List<String> PRIVATE_TABLES = Arrays.asList(
            "ai_model",
            "ai_prompt_config",
            "astrsomn_job_log"
    );

    @Override
    public Expression getTenantId() {
        // 从配置中获取当前环境的 Code (如: dev, prod)
        String code = properties.getEnvCode();
        if (code == null) {
            // 注意：如果返回 null，MP 在某些版本可能会抛错，建议配置默认值或校验
            return new StringValue("default");
        }
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
