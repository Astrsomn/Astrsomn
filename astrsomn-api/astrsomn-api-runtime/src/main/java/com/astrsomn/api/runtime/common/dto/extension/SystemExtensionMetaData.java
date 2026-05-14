package com.astrsomn.api.runtime.common.dto.extension;

public record SystemExtensionMetaData(
        String extensionKey,
        String extensionName,
        String type,
        String version,
        String author,
        String description,
        String avatar,
        String extensionCode,
        String changelog,
        String minServerVersion) {
}