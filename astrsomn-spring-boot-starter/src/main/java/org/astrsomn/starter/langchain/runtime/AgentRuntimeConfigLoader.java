package org.astrsomn.starter.langchain.runtime;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
//import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.constant.AiInstanceEnum;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.common.langchain.buildParam.setting.ToolSetting;
import org.astrsomn.core.common.util.JsonUtil;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.langchain.quota.ModelQuotaManager;
import org.astrsomn.starter.langchain.runtime.strategy.AbstractEntityHandler;
import org.astrsomn.starter.langchain.runtime.strategy.AgentEntityHandler;
import org.astrsomn.starter.langchain.runtime.strategy.InstanceEntityHandler;
import org.astrsomn.starter.langchain.runtime.strategy.ModelEntityHandler;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 从数据库加载 Agent 配置并合并进 {@link AstroChatParam}（模型采样参数、工具/MCP 引用等）。
 * 合并规则：调用方在 {@link AstroChatParam} 中已设置的项优先，其余用库中 Agent 记录补齐。
 */
@Component
@RequiredArgsConstructor
public class AgentRuntimeConfigLoader {

    private final AiAgentMapper aiAgentMapper;
    private final AstrsomnProperties astrsomnProperties;

    private final AiInstanceMapper aiInstanceMapper;
    private final AiAccountMapper aiAccountMapper;
    private final ModelQuotaManager quotaManager;
    private final AiModelMapper modelMapper;

    public <T> void validateAndApplyAgent(AstroChatParam<T> param) {
        if (StringUtils.isBlank(param.getUserMessage())) {
            throw new RuntimeException("用户消息不能为空");
        }
        if (StringUtils.isBlank(param.getAgentKey())) {
            throw new RuntimeException("智能体Key不能为空");
        }
        if (StringUtils.isBlank(param.getMemoryKey())) {
            param.setMemoryKey(UUID.fastUUID().toString());
        }


        AbstractEntityHandler<AgentEntityHandler> agentHandler = new AgentEntityHandler();

        AbstractEntityHandler<AiInstanceEntity> instanceHandler = new InstanceEntityHandler(aiInstanceMapper);
        instanceHandler.execute(param);

        AbstractEntityHandler<AiModelEntity> modelHandler = new ModelEntityHandler(modelMapper);
        modelHandler.execute(param);
    }




}
