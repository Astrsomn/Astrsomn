package com.astrsomn.starter.runtime.langchain.runtime.chain.handler;

import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.langchain.exception.ErrorCode;
import com.astrsomn.starter.runtime.langchain.exception.InvalidRouteConfigException;
import com.astrsomn.starter.runtime.langchain.exception.ModelNotFoundException;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeChainHandler;
import com.astrsomn.starter.runtime.langchain.runtime.chain.AgentRuntimeContext;
import com.astrsomn.starter.runtime.langchain.runtime.chain.RuntimeChatParamMergeSupport;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 按 modelKey 加载模型定义，合并连接相关字段（URL、厂商等）。
 */
@Component
@Order(50)
@RequiredArgsConstructor
public class ResolveModelChainHandler implements AgentRuntimeChainHandler {

    private final AstAiModelMapper aiModelMapper;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String modelKey = StringUtils.trimToNull(ctx.getParam().getModelKey());
        if (modelKey == null) {
            throw new InvalidRouteConfigException(ErrorCode.MODEL_KEY_MISSING,
                    "Please configure instance MODEL_KEY, or maintain an AI_MODEL record with IS_DEFAULT=1 and MODEL_TYPE=chat");
        }
        AiModelEntity model = aiModelMapper.selectOne(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getModelKey, modelKey)
                        .eq(AiModelEntity::getEnvCode, ctx.getEnvCode())
                        .eq(AiModelEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (model == null) {
            throw new ModelNotFoundException(modelKey, ctx.getEnvCode());
        }
        ctx.setModel(model);
        RuntimeChatParamMergeSupport.mergeModelSettingFromModel(ctx.getParam().getModelSetting(), model);
    }
}
