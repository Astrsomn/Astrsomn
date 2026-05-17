package com.astrsomn.api.runtime.common.langchain.buildParam.setting;


import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RagSetting {


    private Integer maxResults;


    private Double minScore;


    private boolean enabled = false;


    private List<String> knowledgeKeys;


    private String embeddingModelKey;

    private String host;


    private String namespace;
}