package com.astrsomn.core.common.langchain.buildParam.setting;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RagSetting {

    /**
     *
     */
    private Integer maxResults;

    /**
     *
     */
    private Double minScore;

    /**
     * 是否开启
     */
    private boolean enabled = false;


    /**
     * 知识库列表
     */
    private List<String> knowledgeKeys;

    /**
     * 嵌入模型 key（与 {@code AI_MODEL.MODEL_KEY} 对应）；为空时可由向量运行时从集合 {@code modelKey} 推断。
     */
    private String embeddingModelKey;

    private String host;



    private String namespace;
}
