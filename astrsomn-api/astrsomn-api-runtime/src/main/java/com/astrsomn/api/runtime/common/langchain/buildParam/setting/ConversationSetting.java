package com.astrsomn.api.runtime.common.langchain.buildParam.setting;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationSetting {

    
    private boolean enableNetwork = false;

    
    private boolean enableDeepThinking = false;

    
    private boolean enableStream = false;


    
    public boolean enableImageGenerate = false;
}