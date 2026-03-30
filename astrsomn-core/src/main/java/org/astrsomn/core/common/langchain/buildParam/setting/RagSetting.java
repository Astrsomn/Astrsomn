package org.astrsomn.core.common.langchain.buildParam.setting;


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



    private String host;



    private String namespace;
}
