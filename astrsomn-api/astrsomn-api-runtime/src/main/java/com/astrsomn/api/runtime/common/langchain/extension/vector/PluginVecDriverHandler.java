package com.astrsomn.api.runtime.common.langchain.extension.vector;

import java.util.List;

public interface PluginVecDriverHandler {

    void applyPluginDrivers(String jarName, List<VecDriver> drivers);

    void removePluginDrivers(String jarName);
}
