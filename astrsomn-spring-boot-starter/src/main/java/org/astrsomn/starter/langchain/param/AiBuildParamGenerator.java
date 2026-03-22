package org.astrsomn.starter.langchain.param;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import jakarta.annotation.Resource;
import org.astrsomn.core.common.langchain.buildParam.AiChatBuildParam;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AiBuildParamGenerator {

    @Resource
    private AiAgentMapper aiAgentMapper;


    public AiChatBuildParam getBuildParam() {
        return new AiChatBuildParam();
    }
}

