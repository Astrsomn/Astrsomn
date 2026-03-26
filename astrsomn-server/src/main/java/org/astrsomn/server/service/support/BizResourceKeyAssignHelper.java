package org.astrsomn.server.service.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.base.BaseEntity;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiMcpEntity;
import org.astrsomn.core.common.entity.AiPromptEntity;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.astrsomn.core.mapper.AiMcpMapper;
import org.astrsomn.core.mapper.AiPromptMapper;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

/**
 * 各资源创建时若业务 key 为空则自动生成；规则见 {@link BizResourceKeyGenerator}。
 */
@Component
@RequiredArgsConstructor
public class BizResourceKeyAssignHelper {

    private final BizResourceKeyGenerator bizResourceKeyGenerator;
    private final AstrsomnProperties astrsomnProperties;
    private final AiAgentMapper aiAgentMapper;
    private final AiPromptMapper aiPromptMapper;
    private final AiToolMapper aiToolMapper;
    private final AiMcpMapper aiMcpMapper;

    public void assignAgentKeyIfBlank(AiAgentEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getAgentKey());
        if (trimmed != null) {
            entity.setAgentKey(trimmed);
            return;
        }
        fillEnv(entity);
        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), astrsomnProperties.getEnvCode());
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        String name = StringUtils.defaultIfBlank(entity.getAgentName(), "agent");
        entity.setAgentKey(
                bizResourceKeyGenerator.generateUniqueBizKey(
                        BizKeyNamespace.AGENT,
                        user,
                        name,
                        candidate ->
                                aiAgentMapper.selectCount(
                                        new LambdaQueryWrapper<AiAgentEntity>()
                                                .eq(AiAgentEntity::getEnvCode, env)
                                                .eq(AiAgentEntity::getCreateUser, user)
                                                .eq(AiAgentEntity::getAgentKey, candidate))));
    }

    public void assignPromptKeyIfBlank(AiPromptEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getPromptKey());
        if (trimmed != null) {
            entity.setPromptKey(trimmed);
            return;
        }
        fillEnv(entity);
        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), astrsomnProperties.getEnvCode());
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        String name = StringUtils.defaultIfBlank(entity.getPromptTitle(), "prompt");
        entity.setPromptKey(
                bizResourceKeyGenerator.generateUniqueBizKey(
                        BizKeyNamespace.PROMPT,
                        user,
                        name,
                        candidate ->
                                aiPromptMapper.selectCount(
                                        new LambdaQueryWrapper<AiPromptEntity>()
                                                .eq(AiPromptEntity::getEnvCode, env)
                                                .eq(AiPromptEntity::getCreateUser, user)
                                                .eq(AiPromptEntity::getPromptKey, candidate))));
    }

    public void assignToolKeyIfBlank(AiToolEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getToolKey());
        if (trimmed != null) {
            entity.setToolKey(trimmed);
            return;
        }
        fillEnv(entity);
        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), astrsomnProperties.getEnvCode());
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        String name = StringUtils.defaultIfBlank(entity.getToolName(), "tool");
        entity.setToolKey(
                bizResourceKeyGenerator.generateUniqueBizKey(
                        BizKeyNamespace.TOOL,
                        user,
                        name,
                        candidate ->
                                aiToolMapper.selectCount(
                                        new LambdaQueryWrapper<AiToolEntity>()
                                                .eq(AiToolEntity::getEnvCode, env)
                                                .eq(AiToolEntity::getCreateUser, user)
                                                .eq(AiToolEntity::getToolKey, candidate))));
    }

    public void assignMcpKeyIfBlank(AiMcpEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getMcpKey());
        if (trimmed != null) {
            entity.setMcpKey(trimmed);
            return;
        }
        fillEnv(entity);
        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), astrsomnProperties.getEnvCode());
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        String name = StringUtils.defaultIfBlank(entity.getServerName(), "mcp");
        entity.setMcpKey(
                bizResourceKeyGenerator.generateUniqueBizKey(
                        BizKeyNamespace.MCP,
                        user,
                        name,
                        candidate ->
                                aiMcpMapper.selectCount(
                                        new LambdaQueryWrapper<AiMcpEntity>()
                                                .eq(AiMcpEntity::getEnvCode, env)
                                                .eq(AiMcpEntity::getCreateUser, user)
                                                .eq(AiMcpEntity::getMcpKey, candidate))));
    }

    private void fillEnv(BaseEntity<?> entity) {
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(astrsomnProperties.getEnvCode());
        }
    }
}
