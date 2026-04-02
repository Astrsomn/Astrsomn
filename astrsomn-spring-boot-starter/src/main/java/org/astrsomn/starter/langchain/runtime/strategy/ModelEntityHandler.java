package org.astrsomn.starter.langchain.runtime.strategy;

import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.mapper.AiModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelEntityHandler extends AbstractEntityHandler{

    private final AiModelMapper aiModelMapper;
    private  AstroChatParam chatParam;

    @Override
    protected Object doQuery(AstroChatParam chatParam) {
        return null;
    }

    @Override
    protected Object doFallback() {
        return null;
    }
}
