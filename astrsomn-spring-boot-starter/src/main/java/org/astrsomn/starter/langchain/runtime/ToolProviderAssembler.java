package org.astrsomn.starter.langchain.runtime;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.tool.UnionToolProvider;
import org.astrsomn.starter.langchain.tool.local.DynamicToolProvider;
import org.astrsomn.starter.langchain.tool.local.LocalToolCacheManager;
import org.astrsomn.starter.langchain.tool.mcp.DynamicMcpToolProvider;
import org.astrsomn.starter.langchain.tool.mcp.McpToolCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 根据 {@link ToolSetting} 查询 MCP / 本地工具配置并组装为 {@link UnionToolProvider}。
 */
@Component
@RequiredArgsConstructor
public class ToolProviderAssembler {

    private final McpToolCacheManager mcpToolManager;
    private final AiMcpMapper aiMcpMapper;
    private final AiToolMapper aiToolMapper;
    private final ApplicationContext applicationContext;
    private final LocalToolCacheManager globalToolCache;
    private final AstrsomnProperties astrsomnProperties;

    public Optional<ToolProvider> assemble(AstroChatParam<?> param) {
        ToolSetting toolSetting = param.getToolSetting();
        if (toolSetting == null) {
            return Optional.empty();
        }

        List<ToolProvider> providers = new ArrayList<>();
        String env = astrsomnProperties.getEnvCode();

        if (toolSetting.getMcpKeys() != null && !toolSetting.getMcpKeys().isEmpty()) {
            List<AiMcpEntity> mcpConfigs = aiMcpMapper.selectList(
                    new LambdaQueryWrapper<AiMcpEntity>()
                            .in(AiMcpEntity::getMcpKey, toolSetting.getMcpKeys())
                            .eq(AiMcpEntity::getEnvCode, env));
            providers.add(new DynamicMcpToolProvider(mcpConfigs, mcpToolManager));
        }
        if (toolSetting.getToolKeys() != null && !toolSetting.getToolKeys().isEmpty()) {
            List<AiToolEntity> toolConfigs = aiToolMapper.selectList(
                    new LambdaQueryWrapper<AiToolEntity>()
                            .in(AiToolEntity::getToolKey, toolSetting.getToolKeys())
                            .eq(AiToolEntity::getEnvCode, env));
            providers.add(new DynamicToolProvider(toolConfigs, applicationContext, globalToolCache));
        }

        if (CollectionUtil.isEmpty(providers)) {
            return Optional.empty();
        }
        return Optional.of(new UnionToolProvider(providers));
    }
}
