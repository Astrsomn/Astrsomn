package org.astrsomn.core.common.dto.chat;


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
    private String memoryKey;

    /**
     *
     */
    private String agentKey;

    /**
     *
     */
    private String modelKey;

    /**
     * 图片链接列表
     */
    private List<String> fileUrlList;

    /**
     * 用户消息
     */
    private String userMessage;
}
