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
        SPRING_BEAN("SPRING_BEAN", "Spring Bean发现");
        private String code;
        private String desc;
    }

    @Getter
    @AllArgsConstructor
    enum InstallSourceEnum implements BaseEnum {
        CLASSPATH_DEPENDENCY("CLASSPATH_DEPENDENCY", "Classpath依赖"),
        PLUGIN_JAR_UPLOAD("PLUGIN_JAR_UPLOAD", "上传插件包"),
        PLUGIN_JAR_DISCOVERED("PLUGIN_JAR_DISCOVERED", "插件目录自动发现");
        private String code;
        private String desc;
    }


}
