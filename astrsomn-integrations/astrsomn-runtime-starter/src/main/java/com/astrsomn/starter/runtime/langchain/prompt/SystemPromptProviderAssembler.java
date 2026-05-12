package com.astrsomn.starter.runtime.langchain.prompt;

import com.astrsomn.starter.runtime.mapper.AstAiPromptMapper;
import lombok.RequiredArgsConstructor;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.api.runtime.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.runtime.config.AstrsomnProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SystemPromptProviderAssembler {

    private final AstAiPromptMapper aiPromptMapper;
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
