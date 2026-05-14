package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class EmbeddingSetting {

    
    private Integer dimensions;

    
    private String user;

    
    private Integer maxRetries;

    
    private Integer maxSegmentsPerBatch;

    
    private String encodingFormat;

    
    private Integer timeoutSeconds;
}