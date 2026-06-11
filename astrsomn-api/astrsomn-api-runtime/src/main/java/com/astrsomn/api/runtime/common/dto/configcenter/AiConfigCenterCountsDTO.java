package com.astrsomn.api.runtime.common.dto.configcenter;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiConfigCenterCountsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long aiAccountCount;

    private Long aiPromptCount;

    private Long aiMcpCount;

    private Long aiToolCount;

    private Long aiTemplateCount;

    private Long aiChatSessionCount;
}
