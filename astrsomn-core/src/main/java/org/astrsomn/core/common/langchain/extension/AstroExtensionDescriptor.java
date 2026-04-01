package org.astrsomn.core.common.langchain.extension;

import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.AiModelEntity;

import java.util.List;

public abstract class AstroExtensionDescriptor {

    /**
     * 默认版本号，如果子类未重写 {@link #getVersion()}，则统一使用该值。
     */
    public static final String DEFAULT_VERSION = "1.0.0";

    public abstract String getExtensionKey();

    public abstract SystemExtensionEnum.ExtensionTypeEnum getExtensionType();


    public abstract String getName();

    /**
     * 扩展版本号，默认返回 {@link #DEFAULT_VERSION}。
     * 子类可以根据需要进行重写。
     */
    public String getVersion() {
        return DEFAULT_VERSION;
    }

    /**
     * 扩展作者，默认返回 Astrsomn。
     */
    public String getAuthor() {
        return "Astrsomn";
    }

    /**
     * 扩展描述，默认空字符串。
     */
    public String getDescription() {
        return "";
    }

    public abstract List<AiModelEntity> getSupportedModels();

}
