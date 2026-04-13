package org.astrsomn.starter.langchain.tool;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ConversationSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.factory.AstroModelFactory;

import org.astrsomn.starter.langchain.tool.image.DynamicImageToolProvider;
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
    private final AstroModelFactory astroModelFactory;
    public ToolProvider assemble(AstroChatParam<?> param) {
        return Optional.ofNullable(param.getToolSetting())
                .map(setting -> {
                    List<ToolProvider> providers = new ArrayList<>();
                    String env = astrsomnProperties.getEnvCode();

                    Optional.ofNullable(setting.getMcpKeys())
                            .filter(CollectionUtil::isNotEmpty)
                            .map(keys -> aiMcpMapper.selectList(new LambdaQueryWrapper<AiMcpEntity>()
                                    .in(AiMcpEntity::getMcpKey, keys)
                                    .eq(AiMcpEntity::getEnvCode, env)))
                            .ifPresent(configs -> providers.add(new DynamicMcpToolProvider(mcpToolManager,configs )));

                    Optional.ofNullable(setting.getToolKeys())
                            .filter(CollectionUtil::isNotEmpty)
                            .map(keys -> aiToolMapper.selectList(new LambdaQueryWrapper<AiToolEntity>()
                                    .in(AiToolEntity::getToolKey, keys)
                                    .eq(AiToolEntity::getEnvCode, env)))
                            .ifPresent(configs -> providers.add(new DynamicToolProvider(configs, applicationContext, globalToolCache)));

                    Optional.ofNullable(param.getConversationSetting())
                            .filter(ConversationSetting::isEnableImageGenerate)
                            .map(s -> astroModelFactory.createModel(param, ImageModel.class))
                            .ifPresent(model -> providers.add(new DynamicImageToolProvider(model)));

                    return providers;
                })
                .filter(CollectionUtil::isNotEmpty)
                .map(UnionToolProvider::new)
                .orElse(null);
    }
}
