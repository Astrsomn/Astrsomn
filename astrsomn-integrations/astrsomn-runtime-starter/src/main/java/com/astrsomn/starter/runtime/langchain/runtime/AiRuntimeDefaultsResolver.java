package com.astrsomn.starter.runtime.langchain.runtime;

import com.astrsomn.api.runtime.common.constant.AiAgentEnum;
import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.entity.AiAgentEntity;
import com.astrsomn.api.runtime.common.entity.AiModelEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AstAiAgentMapper;
import com.astrsomn.starter.runtime.mapper.AstAiModelMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@RequiredArgsConstructor
public class AiRuntimeDefaultsResolver {

    private static final String DEFAULT_FLAG = AiAgentEnum.IsDefaultEnum.YES.getCode();

    private final AstAiAgentMapper aiAgentMapper;
    private final AstAiModelMapper aiModelMapper;

    public Optional<String> resolveDefaultAgentKey(String envCode) {
        if (StringUtils.isBlank(envCode)) {
            return Optional.empty();
        }
        AiAgentEntity row = aiAgentMapper.selectOne(
                new LambdaQueryWrapper<AiAgentEntity>()
                        .eq(AiAgentEntity::getEnvCode, envCode.trim())
                        .eq(AiAgentEntity::getDeleted, false)
                        .eq(AiAgentEntity::getIsDefault, DEFAULT_FLAG)
                        .last("LIMIT 1"));
        if (row == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(StringUtils.trimToNull(row.getAgentKey()));
    }

    public Optional<String> resolveDefaultChatModelKey(String envCode) {
        if (StringUtils.isBlank(envCode)) {
            return Optional.empty();
        }
        String chatType = AiModelEnum.ModelTypeEnum.CHAT_MODEL.getCode();
        AiModelEntity row = aiModelMapper.selectOne(
                new LambdaQueryWrapper<AiModelEntity>()
                        .eq(AiModelEntity::getEnvCode, envCode.trim())
                        .eq(AiModelEntity::getDeleted, false)
                        .eq(AiModelEntity::getIsDefault, DEFAULT_FLAG)
                        .eq(AiModelEntity::getModelType, chatType)
                        .last("LIMIT 1"));
        if (row == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(StringUtils.trimToNull(row.getModelKey()));
    }
}
