package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class ModelSetting {

    
    private String modelName;

    
    private String apiUrl;

    
    private String apiKey;

    
    private String apiSecret;

    
    private String extensionCode;

    
    private String accountKey;
}