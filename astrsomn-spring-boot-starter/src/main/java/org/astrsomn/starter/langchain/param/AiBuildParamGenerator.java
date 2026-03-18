package org.astrsomn.starter.langchain.param;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import domain.dto.request.chat.AiChatQueryRequest;
import domain.entity.AiAgentEntity;
import jakarta.annotation.Resource;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AiBuildParamGenerator {

    @Resource
    private AiAgentMapper aiAgentMapper;


    public AiChatBuildParam getBuildParam(AiChatQueryRequest request) {
        AiAgentEntity agent = aiAgentMapper.selectById(request.getAgentId());
        // 健壮性第一步：确保 Agent 存在（使用 Hutool Assert）
        Assert.notNull(agent, "未找到对应的 Agent 配置，ID: {}", request.getAgentId());

        AiChatBuildParam buildParam = new AiChatBuildParam();
        buildParam.setPromptUuid(agent.getPromptUuid());
        buildParam.setModelId(Objects.nonNull(request.getModelId()) ? request.getModelId() : agent.getModelId());
        buildParam.setEnableDeepSeek(request.isEnableDeepSeek());
        buildParam.setMaxToken(agent.getMaxToken());
        buildParam.setToolIdList(Convert.toList(Long.class, agent.getToolIds()));
        buildParam.setMcpIdList(Convert.toList(Long.class, agent.getMcpIds()));
        buildParam.setMemoryId(request.getMemoryId());
        buildParam.setMessage(request.getMessage());
        buildParam.setEnableStream(agent.isEnableStream());
        buildParam.setSeed(agent.getSeed());
        buildParam.setPresencePenalty(agent.getPresencePenalty());
        try {
            Class clazz = Class.forName(agent.getInterfaceClass());
            buildParam.setClazz(clazz);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }finally {
            buildParam.setClazz(ChatAssistant.class);
        }
        return buildParam;
    }
}

