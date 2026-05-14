package com.astrsomn.api.runtime.common.dto.extension;

import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;


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
        String minServerVersion) {}