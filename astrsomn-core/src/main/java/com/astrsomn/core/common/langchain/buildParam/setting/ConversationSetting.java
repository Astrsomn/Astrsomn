package com.astrsomn.core.common.langchain.buildParam.setting;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConversationSetting {

    /**
     * 开启联网检索
     */
    private boolean enableNetwork = false;

    /**
     * 开启深度思考 (如 o1, deepseek-r1)
     */
    private boolean enableDeepThinking = false;

    /**
     * 开启流式输出
     */
    private boolean enableStream = false;


    /**
     * 开启图像生成
     */
    public boolean enableImageGenerate = false;
}
