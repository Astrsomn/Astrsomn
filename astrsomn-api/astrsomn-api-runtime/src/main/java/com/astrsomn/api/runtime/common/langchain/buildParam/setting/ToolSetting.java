package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ToolSetting {

    private List<String> toolKeys;


    private List<String> mcpKeys;


    private List<String> ragKeys;


    private Integer vectorSize;


}