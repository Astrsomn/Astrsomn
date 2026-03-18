package org.astrsomn.core.common.langchain.buildParam;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChatFeatureFlags {

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


}
