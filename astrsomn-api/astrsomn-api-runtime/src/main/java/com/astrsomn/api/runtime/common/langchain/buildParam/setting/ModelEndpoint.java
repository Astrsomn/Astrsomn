package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 单条模型连接（多节点路由中的一端），与 {@link ModelSetting} 中单组 url/key 语义一致。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ModelEndpoint {

    /** 可选，用于日志与监控打标 */
    private String name;

    private String apiUrl;

    private String apiKey;

    private String apiSecret;

    /** 加权随机时使用，默认 1 */
    private Integer weight;
}
