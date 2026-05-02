package com.astrsomn.starter.runtime.langchain.prompt;

import lombok.RequiredArgsConstructor;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import com.astrsomn.starter.runtime.mapper.AiPromptMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SystemPromptProviderAssembler {

    private final AiPromptMapper aiPromptMapper;
    private final AstrsomnProperties astrsomnProperties;

    public String assemble(AstroChatParam<?> param) {
        return Optional.ofNullable(param)
                .map(AstroChatParam::getPromptSetting)
                .map(setting -> StringUtils.trimToNull(setting.getPromptKey()))
                .map(aiPromptMapper::getLatestPromptContentByPromptKey)
                .map(StringUtils::trimToNull)
                .orElse(null);
    }
}
