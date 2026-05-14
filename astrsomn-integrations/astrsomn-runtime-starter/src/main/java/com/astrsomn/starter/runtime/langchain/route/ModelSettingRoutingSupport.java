package com.astrsomn.starter.runtime.langchain.route;

import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelEndpoint;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelRouteSetting;
import com.astrsomn.api.runtime.common.langchain.buildParam.setting.ModelSetting;
import com.astrsomn.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 从 {@link ModelSetting} 复制连接字段，用于为每个 endpoint 构建独立 LangChain4j 模型实例。
 */
public final class ModelSettingRoutingSupport {

    private ModelSettingRoutingSupport() {
    }

    public static ModelSetting copyModelSetting(ModelSetting src) {
        if (src == null) {
            return new ModelSetting();
        }
        ModelSetting d = new ModelSetting();
        d.setModelName(src.getModelName());
        d.setApiUrl(src.getApiUrl());
        d.setApiKey(src.getApiKey());
        d.setApiSecret(src.getApiSecret());
        d.setExtensionCode(src.getExtensionCode());
        d.setAccountKey(src.getAccountKey());
        d.setModelRouteSetting(null);
        return d;
    }

    public static void applyEndpoint(ModelSetting target, ModelEndpoint ep) {
        if (target == null || ep == null) {
            return;
        }
        if (StringUtils.isNotBlank(ep.getApiUrl())) {
            target.setApiUrl(ep.getApiUrl().trim());
        }
        if (StringUtils.isNotBlank(ep.getApiKey())) {
            target.setApiKey(ep.getApiKey().trim());
        }
        if (StringUtils.isNotBlank(ep.getApiSecret())) {
            target.setApiSecret(ep.getApiSecret().trim());
        }
    }

    public static List<ModelEndpoint> resolveEndpoints(ModelRouteSetting route, ModelSetting base) {
        List<ModelEndpoint> fromRoute = route != null ? route.getEndpoints() : null;
        if (fromRoute != null && !fromRoute.isEmpty()) {
            return new ArrayList<>(fromRoute);
        }
        ModelEndpoint single = new ModelEndpoint();
        single.setName("default");
        single.setApiUrl(base != null ? base.getApiUrl() : null);
        single.setApiKey(base != null ? base.getApiKey() : null);
        single.setApiSecret(base != null ? base.getApiSecret() : null);
        single.setWeight(1);
        return List.of(single);
    }
}
