package com.astrsomn.server.service.system.extension.lifecycle;

import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.exception.SystemExtensionErrorEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ExtensionStrategyResolver {

    private final Map<SystemExtensionEnum.ExtensionTypeEnum, ExtensionLifecycleStrategy> strategyMap =
            new EnumMap<>(SystemExtensionEnum.ExtensionTypeEnum.class);

    public ExtensionStrategyResolver(List<ExtensionLifecycleStrategy> strategies) {
        for (ExtensionLifecycleStrategy strategy : strategies) {
            strategyMap.put(strategy.supportType(), strategy);
        }
    }

    public ExtensionLifecycleStrategy resolve(String typeCode) {
        String normalized = StringUtils.trimToNull(typeCode);
        if (normalized == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "扩展类型不能为空");
        }
        SystemExtensionEnum.ExtensionTypeEnum type = null;
        for (SystemExtensionEnum.ExtensionTypeEnum e : SystemExtensionEnum.ExtensionTypeEnum.values()) {
            if (e.getCode().equals(normalized)) {
                type = e;
                break;
            }
        }
        if (type == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "未知扩展类型: " + normalized);
        }
        ExtensionLifecycleStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "未注册扩展策略: " + normalized);
        }
        return strategy;
    }
}

