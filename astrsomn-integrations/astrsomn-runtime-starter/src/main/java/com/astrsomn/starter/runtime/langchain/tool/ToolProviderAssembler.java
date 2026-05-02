package com.astrsomn.starter.runtime.langchain.tool;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.service.tool.ToolProvider;
import lombok.RequiredArgsConstructor;
import com.astrsomn.api.runtime.common.entity.AiMcpEntity;
import com.astrsomn.api.runtime.common.entity.AiToolEntity;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ConversationSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ToolSetting;
import com.astrsomn.starter.runtime.mapper.AiMcpMapper;
import com.astrsomn.starter.runtime.mapper.AiToolMapper;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.langchain.factory.AstroModelFactory;

import com.astrsomn.starter.runtime.langchain.tool.image.DynamicImageToolProvider;
import com.astrsomn.starter.runtime.langchain.tool.local.DynamicToolProvider;
import com.astrsomn.starter.runtime.langchain.tool.local.LocalToolCacheManager;
import com.astrsomn.starter.runtime.langchain.tool.mcp.DynamicMcpToolProvider;
import com.astrsomn.starter.runtime.langchain.tool.mcp.McpToolCacheManager;
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
