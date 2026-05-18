package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.ErrorCode;
import com.astrsomn.starter.runtime.langchain.exception.InstanceNotFoundException;
import com.astrsomn.starter.runtime.langchain.exception.InvalidRouteConfigException;
import com.astrsomn.starter.runtime.langchain.runtime.AiRuntimeDefaultsResolver;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiInstanceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(40)
@RequiredArgsConstructor
public class ResolveInstanceChainHandler implements AgentRuntimeChainHandler {

    private final AstAiInstanceMapper aiInstanceMapper;
    private final AiRuntimeDefaultsResolver aiRuntimeDefaultsResolver;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String instanceKey = StringUtils.trimToNull(ctx.getParam().getInstanceKey());
        if (instanceKey == null) {
            throw new InvalidRouteConfigException(ErrorCode.INSTANCE_KEY_MISSING,
                    "Please configure instanceKey in request or agent CHAT_INSTANCE_KEY");
        }
        AiInstanceEntity instance = aiInstanceMapper.selectOne(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getInstanceKey, instanceKey)
                        .eq(AiInstanceEntity::getEnvCode, ctx.getEnvCode())
                        .eq(AiInstanceEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (instance == null) {
            throw new InstanceNotFoundException(instanceKey, ctx.getEnvCode());
        }
        ctx.setInstance(instance);
        RuntimeChatParamMergeSupport.mergeChatSettingFromInstance(ctx.getParam().getChatSetting(), instance);
        if (StringUtils.isNotBlank(instance.getModelRouteJson())) {
            RuntimeChatParamMergeSupport.mergeModelRouteFromJson(ctx.getParam().getModelSetting(), instance.getModelRouteJson());
        }
        if (StringUtils.isBlank(ctx.getParam().getModelKey())) {
            ctx.getParam().setModelKey(StringUtils.trimToNull(instance.getModelKey()));
        }
        if (StringUtils.isBlank(ctx.getParam().getModelKey())) {
            aiRuntimeDefaultsResolver
                    .resolveDefaultChatModelKey(ctx.getEnvCode())
                    .ifPresent(ctx.getParam()::setModelKey);
        }
    }
}
