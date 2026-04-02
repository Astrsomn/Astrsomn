package org.astrsomn.starter.langchain.runtime.strategy;

import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.springframework.stereotype.Component;

@Component
public class AccountEntityHandler extends AbstractEntityHandler{
    @Override
    protected Object doQuery(AstroChatParam chatParam) {
        return null;
    }

    @Override
    protected Object doFallback() {
        return null;
    }
}
