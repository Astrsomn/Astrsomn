package com.astrsomn.core.common.dto.extension;

import com.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

/**
 * 从插件 jar 内 {@code META-INF/services} 解析出的扩展展示元数据（与 {@link AstroExtensionDescriptor} 等一致）。
 */
public record SystemExtensionMetaData(
        String extensionKey,
        String extensionName,
        String type,
        String version,
        String author,
        String description,
        String avatar,
        String providerCode,
        String changelog,
        String minServerVersion) {}
