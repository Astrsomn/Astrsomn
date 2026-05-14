package com.astrsomn.api.runtime.common.langchain.buildParam.setting;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationSetting {


    public boolean enableImageGenerate = false;
    private boolean enableNetwork = false;
    private boolean enableDeepThinking = false;
    private boolean enableStream = false;
}