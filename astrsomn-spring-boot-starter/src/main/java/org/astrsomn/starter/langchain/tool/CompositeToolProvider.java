package org.astrsomn.starter.langchain.tool;

import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderRequest;
import dev.langchain4j.service.tool.ToolProviderResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompositeToolProvider implements ToolProvider{


    public ToolProviderResult buildTools(){
        return null;
    }


    @Override
    public ToolProviderResult provideTools(ToolProviderRequest toolProviderRequest) {
        return null;
    }
}
