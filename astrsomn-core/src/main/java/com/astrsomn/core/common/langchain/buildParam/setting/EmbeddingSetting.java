package com.astrsomn.core.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 嵌入模型调用参数，与 LangChain4j {@code OpenAiEmbeddingModel} Builder 可对齐。
 */
@Data
@Accessors(chain = true)
public class EmbeddingSetting {

    /**
     * 输出向量维度（如 text-embedding-3-small 可截断维度），对应 {@code .dimensions()}.
     */
    private Integer dimensions;

    /**
     * 终端用户标识，对应 {@code .user()}.
     */
    private String user;

    /**
     * 失败重试次数，对应 {@code .maxRetries()}.
     */
    private Integer maxRetries;

    /**
     * 单批最大文本条数，对应 {@code .maxSegmentsPerBatch()}.
     */
    private Integer maxSegmentsPerBatch;

    /**
     * 编码格式，对应 {@code .encodingFormat()}（如 float、base64，以 OpenAI API 为准）.
     */
    private String encodingFormat;

    /**
     * HTTP 连接/读超时（秒），用于 {@code .timeout(Duration)}.
     */
    private Integer timeoutSeconds;
}
