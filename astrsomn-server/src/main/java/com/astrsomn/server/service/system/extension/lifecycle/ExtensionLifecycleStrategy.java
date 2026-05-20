package com.astrsomn.server.service.system.extension.lifecycle;

import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.entity.SystemExtensionEntity;


public interface ExtensionLifecycleStrategy {

    SystemExtensionEnum.ExtensionTypeEnum supportType();

    void apply(SystemExtensionEntity extension);

    void revoke(SystemExtensionEntity extension);

    void uninstall(SystemExtensionEntity extension);
}

