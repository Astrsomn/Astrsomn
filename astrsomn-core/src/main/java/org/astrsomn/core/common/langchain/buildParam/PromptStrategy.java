package org.astrsomn.core.common.langchain.buildParam;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PromptStrategy {

    /**
     * 系统预设指令 (System Message)
     */
    private String systemMessage;

    /**
     * 提示词模板 UUID (替代旧的 promptId)
     */
    private String promptUuid;


}
