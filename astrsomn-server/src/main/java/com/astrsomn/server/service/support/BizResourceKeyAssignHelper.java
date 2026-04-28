package com.astrsomn.server.service.support;

import com.astrsomn.core.common.entity.*;
import com.astrsomn.commn.base.BaseEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.context.EnvRuntime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 各资源创建时若业务 key 为空则自动生成；规则见 {@link BizResourceKeyGenerator}。
 */
@Component
@RequiredArgsConstructor
public class BizResourceKeyAssignHelper {

    private final BizResourceKeyGenerator bizResourceKeyGenerator;
    private final AstrsomnProperties astrsomnProperties;

    /**
     * 为 Agent 分配业务键（如果为空）
     */
    public void assignAgentKeyIfBlank(AiAgentEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.AGENT, entity, entity::getAgentKey, entity::setAgentKey);
    }

    /**
     * 为 Prompt 分配业务键（如果为空）
     */
    public void assignPromptKeyIfBlank(AiPromptEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.PROMPT, entity, entity::getPromptKey, entity::setPromptKey);
    }

    /**
     * 为 Tool 分配业务键（如果为空）
     */
    public void assignToolKeyIfBlank(AiToolEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.TOOL, entity, entity::getToolKey, entity::setToolKey);
    }

    /**
     * 为 Instance 分配业务键（如果为空）
     */
    public void assignInstanceKeyIfBlank(AiInstanceEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.INSTANCE, entity, entity::getInstanceKey, entity::setInstanceKey);
    }

    /**
     * 为 Account 分配业务键（如果为空）
     */
    public void assignAccountKeyIfBlank(AiAccountEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.ACCOUNT, entity, entity::getAccountKey, entity::setAccountKey);
    }

    /**
     * 为 Account 分配业务键（更新场景，排除当前记录）
     */
    public void assignAccountKeyIfBlank(AiAccountEntity entity, Long excludeId) {
        assignKeyIfBlank(BizKeyNamespace.ACCOUNT, entity, excludeId, entity::getAccountKey, entity::setAccountKey);
    }

    /**
     * 为 MCP 分配业务键（如果为空）
     */
    public void assignMcpKeyIfBlank(AiMcpEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.MCP, entity, entity::getMcpKey, entity::setMcpKey);
    }

    /**
     * 为 Model 分配业务键（如果为空）
     */
    public void assignModelKeyIfBlank(AiModelEntity entity) {
        assignKeyIfBlank(BizKeyNamespace.MODEL, entity, entity::getModelKey, entity::setModelKey);
    }

    /**
     * 通用方法：为实体分配业务键（如果为空）
     */
    private <T extends BaseEntity<?>> void assignKeyIfBlank(
            BizKeyNamespace namespace,
            T entity,
            java.util.function.Supplier<String> getter,
            java.util.function.Consumer<String> setter) {
        
        String trimmed = StringUtils.trimToNull(getter.get());
        if (trimmed != null) {
            setter.accept(trimmed);
            fillEnv(entity);
            return;
        }
        
        String key = bizResourceKeyGenerator.generateKey(namespace, entity);
        setter.accept(key);
    }

    /**
     * 通用方法：为实体分配业务键（更新场景）
     */
    private <T extends BaseEntity<?>> void assignKeyIfBlank(
            BizKeyNamespace namespace,
            T entity,
            Long excludeId,
            java.util.function.Supplier<String> getter,
            java.util.function.Consumer<String> setter) {
        
        String trimmed = StringUtils.trimToNull(getter.get());
        if (trimmed != null) {
            setter.accept(trimmed);
            fillEnv(entity);
            // 更新场景下如果用户提供了 key，需要检查唯一性
            if (namespace == BizKeyNamespace.ACCOUNT) {
                assertKeyUniqueInEnv(namespace, entity, excludeId, getter.get());
            }
            return;
        }
        
        String key = bizResourceKeyGenerator.generateKey(namespace, entity, excludeId);
        setter.accept(key);
    }

    /**
     * 检查 key 在环境内是否唯一
     */
    private <T extends BaseEntity<?>> void assertKeyUniqueInEnv(
            BizKeyNamespace namespace,
            T entity,
            Long excludeId,
            String key) {
        
        String env = StringUtils.trimToNull(entity.getEnvCode());
        if (env == null) {
            fillEnv(entity);
            env = entity.getEnvCode();
        }
        
        if (key == null || env == null) {
            return;
        }
        
        // 调用 generateKey 但不使用结果，仅检查唯一性
        String generated = bizResourceKeyGenerator.generateKey(namespace, entity, excludeId);
        if (generated == null) {
            throw new IllegalArgumentException("该环境下 " + namespace.getPrefix() + " Key 已存在，请更换后重试");
        }
    }

    /**
     * 设置环境编码
     */
    private void fillEnv(BaseEntity<?> entity) {
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }
    }
}
