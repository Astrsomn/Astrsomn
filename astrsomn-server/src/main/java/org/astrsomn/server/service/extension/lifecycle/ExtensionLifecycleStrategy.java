package org.astrsomn.server.service.extension.lifecycle;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.SystemExtensionEntity;

/**
 * 按扩展类型定义生命周期操作（应用 / 取消应用 / 卸载）的策略接口。
 */
public interface ExtensionLifecycleStrategy {

    SystemExtensionEnum.ExtensionTypeEnum supportType();

    void apply(SystemExtensionEntity extension);

    void revoke(SystemExtensionEntity extension);

    void uninstall(SystemExtensionEntity extension);
}

