package com.astrsomn.core.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模型基础设置
 */
@Data
@Accessors(chain = true)
public class ModelSetting {

    /**
     * 模型名称
     */
    private String modelName;

    /**
     *
     */
    private String apiUrl;

    /**
     *
     */
    private String apiKey;

    /**
     *
     */
    private String apiSecret;

    /**
     *
     */
    private String extensionCode;

    /**
     * 账号Key
     */
    private String accountKey;
}
