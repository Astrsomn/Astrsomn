package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class ChatSetting {


    private Double temperature;


    private Double topP;


    private Integer topK;

    private Integer maxTokens;


    private Integer seed;


    private Double presencePenalty;


    private Double frequencyPenalty;

}