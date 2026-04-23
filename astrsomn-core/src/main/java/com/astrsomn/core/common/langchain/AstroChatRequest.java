package com.astrsomn.core.common.langchain;


import lombok.Data;

import java.util.List;

@Data
public class AstroChatRequest {

    /**
     *
     */
    private boolean enableNetwork;

    /**
     *
     */
    private boolean enableStream;

    /**
     *
     */
    private boolean enableDeepThinking;
    /**
     *
     */
    private String memoryKey;

    /**
     *
     */
    private String agentKey;

    /**
     * 实例Key
     */
    private String instanceKey;
    /**
     * 图片链接列表
     */
    private List<String> fileUrlList;

    /**
     * 用户消息
     */
    private String userMessage;
}
