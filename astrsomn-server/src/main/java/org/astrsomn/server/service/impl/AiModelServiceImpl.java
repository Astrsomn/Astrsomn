package org.astrsomn.server.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatRequest;

import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.server.demo.OrderCreateAssistant;
import org.astrsomn.server.demo.TaskCreateAssistant;
import org.astrsomn.server.service.AiModelService;
import org.astrsomn.starter.langchain.AstroAssistantFactory;
import org.astrsomn.starter.langchain.aop.Astro;
import org.springframework.stereotype.Service;

@Service
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {

    // TODO 调用方式一，构建param
    @Resource
    private AstroAssistantFactory assistantFactory;

    public String createTask(){
        var param = AstroChatRequest.of(TaskCreateAssistant.class, "deepseek-sensor");
        param.getModelSetting().setMaxTokens(100)
                .setTopK(20);
        param.getChatSetting().setEnableDeepThinking(true);

        TaskCreateAssistant assistant = assistantFactory.createAssistant(param);
        String response = assistant.chat("帮我创建一个任务，执行人是刘慧鹏", UUID.fastUUID().toString());
        return response;
    }

    // TODO 调用方式二，注解固定配置
    @Astro(agentKey = "deepseek-chat", envCode = "prod")
    private OrderCreateAssistant orderCreateAssistant;

    public String createOrder(){
        String response = orderCreateAssistant.chat("帮我买一个iPhone17", UUID.randomUUID().toString());
        return response;
    }


}
