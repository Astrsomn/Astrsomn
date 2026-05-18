package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ModelEndpoint {


    private String name;

    private String apiUrl;

    private String apiKey;

    private String apiSecret;


    private Integer weight;
}
