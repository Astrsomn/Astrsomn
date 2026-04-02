package org.astrsomn.starter.langchain.runtime.strategy;


import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import org.astrsomn.core.common.langchain.buildParam.setting.ChatSetting;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstanceEntityHandler extends AbstractEntityHandler{
    private final AiInstanceMapper aiInstanceMapper;




    @Override
    protected Object doQuery(AstroChatParam chatParam) {
        return null;
    }

    @Override
    protected Object doFallback() {
        return null;
    }
    private void mergeModelFromInstance(ChatSetting target, AiInstanceEntity instance) {
        if (instance == null) {
            return;
        }
        if (target.getTemperature() == null) {
            target.setTemperature(instance.getTemperature());
        }
        if (target.getTopP() == null) {
            target.setTopP(instance.getTopP());
        }
        if (target.getTopK() == null) {
            target.setTopK(instance.getTopK());
        }
        if (target.getMaxTokens() == null) {
            target.setMaxTokens(instance.getMaxTokens());
        }
        if (target.getSeed() == null) {
            target.setSeed(instance.getSeed());
        }
        if (target.getPresencePenalty() == null) {
            target.setPresencePenalty(instance.getPresencePenalty());
        }
        if (target.getFrequencyPenalty() == null) {
            target.setFrequencyPenalty(instance.getFrequencyPenalty());
        }
    }


}
