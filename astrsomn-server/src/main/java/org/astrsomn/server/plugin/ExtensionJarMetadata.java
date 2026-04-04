package org.astrsomn.server.plugin;

/**
 * 从插件 jar 内 {@code META-INF/services} 解析出的扩展展示元数据（与 {@link org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor} 等一致）。
 */
public record ExtensionJarMetadata(
        String extensionKey,
        String extensionName,
        String type,
        String version,
        String author,
        String description,
        String avatar,
        String providerCode) {}
