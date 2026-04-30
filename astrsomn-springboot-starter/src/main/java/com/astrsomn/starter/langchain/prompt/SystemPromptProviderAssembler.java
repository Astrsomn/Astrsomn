package com.astrsomn.starter.langchain.prompt;

import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.core.common.langchain.buildParam.AstroChatParam;
import com.astrsomn.starter.config.AstrsomnProperties;
import com.astrsomn.starter.mapper.AiPromptMapper;
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
                .map(promptKey -> aiPromptMapper.getLatestPromptContentByPromptKey(promptKey, astrsomnProperties.getEnvCode()))
                .map(StringUtils::trimToNull)
                .orElse(null);
    }
}
