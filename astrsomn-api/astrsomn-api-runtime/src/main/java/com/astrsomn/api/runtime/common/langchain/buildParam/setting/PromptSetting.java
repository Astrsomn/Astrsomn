package com.astrsomn.api.runtime.common.langchain.buildParam.setting;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PromptSetting {

    
    private String systemMessage;

    
    private String promptKey;


}