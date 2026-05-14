package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageSetting {

    
    private String size;

    
    private String style;

    
    private String quality;

    
    private String responseFormat;

    
    private String user;

    
    private Integer maxRetries;

    
    private Integer timeoutSeconds;
}