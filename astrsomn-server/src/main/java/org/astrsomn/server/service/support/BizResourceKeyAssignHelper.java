package org.astrsomn.server.service.support;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseEntity;
import org.astrsomn.core.common.entity.*;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.*;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
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
    private final AiInstanceMapper aiInstanceMapper;
    private final AiAccountMapper aiAccountMapper;

    public void assignAgentKeyIfBlank(AiAgentEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getAgentKey());
        if (trimmed != null) {
            entity.setAgentKey(trimmed);
            return;
        }
        fillEnv(entity);
        final String envCode = entity.getEnvCode();
        entity.setAgentKey(
                bizResourceKeyGenerator.generateDateBasedBizKey(
                        BizKeyNamespace.AGENT,
                        candidate ->
                                aiAgentMapper.selectCount(
                                        new LambdaQueryWrapper<AiAgentEntity>()
                                                .eq(AiAgentEntity::getEnvCode, envCode)
                                                .eq(AiAgentEntity::getAgentKey, candidate))));
    }

    public void assignPromptKeyIfBlank(AiPromptEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getPromptKey());
        if (trimmed != null) {
            entity.setPromptKey(trimmed);
            return;
        }
        fillEnv(entity);
        final String envCode = entity.getEnvCode();
        entity.setPromptKey(
                bizResourceKeyGenerator.generateDateBasedBizKey(
                        BizKeyNamespace.PROMPT,
                        candidate ->
                                aiPromptMapper.selectCount(
                                        new LambdaQueryWrapper<AiPromptEntity>()
                                                .eq(AiPromptEntity::getEnvCode, envCode)
                                                .eq(AiPromptEntity::getPromptKey, candidate))));
    }

    public void assignToolKeyIfBlank(AiToolEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getToolKey());
        if (trimmed != null) {
            entity.setToolKey(trimmed);
            return;
        }
        fillEnv(entity);
        final String envCode = entity.getEnvCode();
        entity.setToolKey(
                bizResourceKeyGenerator.generateDateBasedBizKey(
                        BizKeyNamespace.TOOL,
                        candidate ->
                                aiToolMapper.selectCount(
                                        new LambdaQueryWrapper<AiToolEntity>()
                                                .eq(AiToolEntity::getEnvCode, envCode)
                                                .eq(AiToolEntity::getToolKey, candidate))));
    }

    public void assignInstanceKeyIfBlank(AiInstanceEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getInstanceKey());
        if (trimmed != null) {
            entity.setInstanceKey(trimmed);
            return;
        }
        fillEnv(entity);
        final String envCode = entity.getEnvCode();
        entity.setInstanceKey(
                bizResourceKeyGenerator.generateDateBasedBizKey(
                        BizKeyNamespace.INSTANCE,
                        candidate ->
                                aiInstanceMapper.selectCount(
                                        new LambdaQueryWrapper<AiInstanceEntity>()
                                                .eq(AiInstanceEntity::getEnvCode, envCode)
                                                .eq(AiInstanceEntity::getInstanceKey, candidate))));
    }

    /**
     * 创建账号时：accountKey 为空则生成；同一 {@code env_code} 下 {@code account_key} 唯一。
     */
    public void assignAccountKeyIfBlank(AiAccountEntity entity) {
        assignAccountKeyIfBlank(entity, null);
    }

    /**
     * @param excludeIdForRegen 更新场景下重新生成 key 时需排除当前行，避免误把自己算作已占用
     */
    public void assignAccountKeyIfBlank(AiAccountEntity entity, Long excludeIdForRegen) {
        String trimmed = StringUtils.trimToNull(entity.getAccountKey());
        if (trimmed != null) {
            entity.setAccountKey(trimmed);
            fillEnv(entity);
            assertAccountKeyUniqueInEnv(entity, excludeIdForRegen);
            return;
        }
        fillEnv(entity);
        final Long excludeId = excludeIdForRegen;
        final String envCode = entity.getEnvCode();
        entity.setAccountKey(
                bizResourceKeyGenerator.generateDateBasedBizKey(
                        BizKeyNamespace.ACCOUNT,
                        candidate -> {
                            LambdaQueryWrapper<AiAccountEntity> w =
                                    new LambdaQueryWrapper<AiAccountEntity>()
                                            .eq(AiAccountEntity::getEnvCode, envCode)
                                            .eq(AiAccountEntity::getAccountKey, candidate);
                            if (excludeId != null) {
                                w.ne(AiAccountEntity::getId, excludeId);
                            }
                            return aiAccountMapper.selectCount(w);
                        }));
    }

    private void assertAccountKeyUniqueInEnv(AiAccountEntity entity, Long excludeId) {
        String env = StringUtils.trimToNull(entity.getEnvCode());
        if (env == null) {
            fillEnv(entity);
            env = entity.getEnvCode();
        }
        String key = StringUtils.trimToNull(entity.getAccountKey());
        if (key == null || env == null) {
            return;
        }
        LambdaQueryWrapper<AiAccountEntity> w =
                new LambdaQueryWrapper<AiAccountEntity>()
                        .eq(AiAccountEntity::getEnvCode, env)
                        .eq(AiAccountEntity::getAccountKey, key);
        if (excludeId != null) {
            w.ne(AiAccountEntity::getId, excludeId);
        }
        if (aiAccountMapper.selectCount(w) > 0) {
            throw new IllegalArgumentException("该环境下 Account Key 已存在，请更换后重试");
        }
    }

    public void assignMcpKeyIfBlank(AiMcpEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getMcpKey());
        if (trimmed != null) {
            entity.setMcpKey(trimmed);
            return;
        }
        fillEnv(entity);
        final String envCode = entity.getEnvCode();
        entity.setMcpKey(
                bizResourceKeyGenerator.generateDateBasedBizKey(
                        BizKeyNamespace.MCP,
                        candidate ->
                                aiMcpMapper.selectCount(
                                        new LambdaQueryWrapper<AiMcpEntity>()
                                                .eq(AiMcpEntity::getEnvCode, envCode)
                                                .eq(AiMcpEntity::getMcpKey, candidate))));
    }

    private void fillEnv(BaseEntity<?> entity) {
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }
    }
}
