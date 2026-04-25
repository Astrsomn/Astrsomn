package com.astrsomn.starter.langchain.runtime.chain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.core.common.utils.StringUtils;
import com.astrsomn.starter.mapper.AiModelMapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 按 modelKey 加载模型定义，合并连接相关字段（URL、厂商等）。
 */
@Component
@Order(50)
@RequiredArgsConstructor
public class ResolveModelChainHandler implements AgentRuntimeChainHandler {

    private final AiModelMapper aiModelMapper;

    @Override
    public void handle(AgentRuntimeContext ctx) {
        String modelKey = StringUtils.trimToNull(ctx.getParam().getModelKey());
        if (modelKey == null) {
            throw new IllegalStateException(
                    "未解析到模型 Key：请配置实例 MODEL_KEY，或在当前环境下维护一条 IS_DEFAULT=1 且 MODEL_TYPE=chat 的 AI_MODEL 记录");
        }
        AiModelEntity model = aiModelMapper.selectOne(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getModelKey, modelKey)
                        .eq(AiModelEntity::getEnvCode, ctx.getEnvCode())
                        .eq(AiModelEntity::getDeleted, false)
                        .last("LIMIT 1"));
        if (model == null) {
            throw new IllegalStateException(
                    "未找到模型配置: modelKey=" + modelKey + ", envCode=" + ctx.getEnvCode());
        }
        ctx.setModel(model);
        RuntimeChatParamMergeSupport.mergeModelSettingFromModel(ctx.getParam().getModelSetting(), model);
    }
}
