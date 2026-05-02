package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageSetting {

    /**
     * 图像尺寸，如 1024x1024，对应 OpenAiImageModel {@code .size()}.
     */
    private String size;

    /**
     * DALL·E 3 风格 vivid / natural，对应 {@code .style()}.
     */
    private String style;

    /**
     * standard / hd 等，对应 {@code .quality()}.
     */
    private String quality;

    /**
     * url / b64_json，对应 {@code .responseFormat()}.
     */
    private String responseFormat;

    /**
     * 终端用户标识，对应 {@code .user()}.
     */
    private String user;

    /**
     * 重试次数，对应 {@code .maxRetries()}.
     */
    private Integer maxRetries;

    /**
     * 请求超时（秒），对应 {@code .timeout(Duration)}.
     */
    private Integer timeoutSeconds;
}

