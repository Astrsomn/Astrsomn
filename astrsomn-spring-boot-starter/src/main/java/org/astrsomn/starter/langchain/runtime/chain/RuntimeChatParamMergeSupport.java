package org.astrsomn.starter.langchain.runtime.chain;

import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ModelSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.PromptSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.common.utils.JsonUtil;
import org.astrsomn.core.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 将各层业务数据以「仅补 null」方式合并进 {@link AstroChatParam}，避免覆盖调用方已设值。
 */
public final class RuntimeChatParamMergeSupport {

    private RuntimeChatParamMergeSupport() {
    }

    public static void mergeChatSettingFromInstance(ChatSetting target, AiInstanceEntity instance) {
        if (instance == null || target == null) {
            return;
        }
        if (target.getTemperature() == null) {
            target.setTemperature(instance.getTemperature());
        }
        if (target.getTopP() == null) {
            target.setTopP(instance.getTopP());
        }
        if (target.getTopK() == null) {
            target.setTopK(instance.getTopK());
        }
        if (target.getMaxTokens() == null) {
            target.setMaxTokens(instance.getMaxTokens());
        }
        if (target.getSeed() == null) {
            target.setSeed(instance.getSeed());
        }
        if (target.getPresencePenalty() == null) {
            target.setPresencePenalty(instance.getPresencePenalty());
        }
        if (target.getFrequencyPenalty() == null) {
            target.setFrequencyPenalty(instance.getFrequencyPenalty());
        }
    }

    public static void mergeModelSettingFromModel(ModelSetting target, AiModelEntity model) {
        if (model == null || target == null) {
            return;
        }
        if (StringUtils.isBlank(target.getModelName())) {
            target.setModelName(StringUtils.trimToNull(model.getModelName()));
        }
        if (StringUtils.isBlank(target.getApiUrl())) {
            target.setApiUrl(StringUtils.trimToNull(model.getApiUrl()));
        }
        if (StringUtils.isBlank(target.getProvider())) {
            target.setProvider(StringUtils.trimToNull(model.getProvider()));
        }
    }

    public static void mergeModelSettingFromAccount(ModelSetting target, AiAccountEntity account) {
        if (account == null || target == null) {
            return;
        }
        if (StringUtils.isBlank(target.getApiKey())) {
            target.setApiKey(StringUtils.trimToNull(account.getApiKey()));
        }
        if (StringUtils.isBlank(target.getApiSecret())) {
            target.setApiSecret(StringUtils.trimToNull(account.getApiSecret()));
        }
    }

    public static void mergeAgentKeysIntoParam(AstroChatParam<?> param, AiAgentEntity agent) {
        if (agent == null || param == null) {
            return;
        }
        if (StringUtils.isBlank(param.getInstanceKey())) {
            param.setInstanceKey(StringUtils.trimToNull(agent.getChatInstanceKey()));
        }
        PromptSetting prompt = param.getPromptSetting();
        if (prompt != null && StringUtils.isBlank(prompt.getPromptKey())) {
            prompt.setPromptKey(StringUtils.trimToNull(agent.getPromptKey()));
        }
        ToolSetting tool = param.getToolSetting();
        if (tool != null) {
            if (isEmpty(tool.getToolKeys())) {
                tool.setToolKeys(parseStringList(agent.getToolKeys()));
            }
            if (isEmpty(tool.getMcpKeys())) {
                tool.setMcpKeys(parseStringList(agent.getMcpKeys()));
            }
            if (isEmpty(tool.getRagKeys())) {
                tool.setRagKeys(parseStringList(agent.getKnowledgeBaseKeys()));
            }
        }
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static List<String> parseStringList(String raw) {
        String trimmed = StringUtils.trimToNull(raw);
        if (trimmed == null) {
            return null;
        }
        try {
            List<String> fromJson = JsonUtil.parseArray(trimmed, String.class);
            return fromJson == null || fromJson.isEmpty() ? null : new ArrayList<>(fromJson);
        } catch (Exception ignored) {
            // 非 JSON 数组时按逗号分隔
        }
        String[] parts = trimmed.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String t = StringUtils.trimToNull(p);
            if (t != null) {
                out.add(t);
            }
        }
        return out.isEmpty() ? null : out;
    }
}
