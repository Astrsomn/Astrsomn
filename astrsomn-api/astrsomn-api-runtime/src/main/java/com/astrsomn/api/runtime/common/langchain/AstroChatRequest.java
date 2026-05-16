package com.astrsomn.api.runtime.common.langchain;

import lombok.Data;

import java.util.List;

@Data
public class AstroChatRequest {


    private boolean enableNetwork;


    private boolean enableStream;


    private boolean enableDeepThinking;

    private String memoryKey;


    private String bizKey;


    private String instanceKey;

    private List<String> fileUrlList;


    private String userMessage;

}