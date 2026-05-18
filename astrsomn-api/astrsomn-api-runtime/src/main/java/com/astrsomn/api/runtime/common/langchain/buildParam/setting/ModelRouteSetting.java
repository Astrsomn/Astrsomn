package com.astrsomn.api.runtime.common.langchain.buildParam.setting;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ModelRouteSetting {

    private boolean enabled = false;


    private String selectionStrategyId = "roundRobin";


    private int failoverMaxAttempts = 2;

    private List<ModelEndpoint> endpoints = new ArrayList<>();


    private String resilienceStrategyId = "noop";


    private String resilienceInstanceNameTemplate = "astroModel-{agentKey}";

    public static ModelRouteSetting disabled() {
        ModelRouteSetting s = new ModelRouteSetting();
        s.setEnabled(false);
        return s;
    }
}
