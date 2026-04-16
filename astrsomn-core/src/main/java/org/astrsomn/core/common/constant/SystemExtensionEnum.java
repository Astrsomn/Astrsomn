package org.astrsomn.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrsomn.core.common.base.BaseEnum;

public interface SystemExtensionEnum {
    @Getter
    @AllArgsConstructor
    enum ExtensionTypeEnum implements BaseEnum {
        MCP("MCP", "MCP"),
        MODEL_PROVIDER("MODEL_PROVIDER", "模型"),
        VECTOR_STORE("VECTOR_STORE", "向量库");
        private String code;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum ExtensionInstallStatusEnum implements BaseEnum {
        INSTALLED("INSTALLED", "已安装"),
        APPLIED("APPLIED", "已应用"),
        UNINSTALLED("UNINSTALLED", "未安装");
        private String code;
        private String desc;

    }

    @Getter
    @AllArgsConstructor
    enum ApplyStatusEnum implements BaseEnum{
        Y("Y", "Y"),
        N("N", "N");
        private String code;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum DiscoveryMechanismEnum implements BaseEnum {
        SPI("SPI", "SPI发现"),
        DEPENDENCY("DEPENDENCY", "依赖声明");
        private String code;
        private String desc;
    }


}
