package com.astrsomn.api.runtime.common.langchain.extension;

import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public abstract class AstroExtensionDescriptor {

    public abstract String getExtensionKey();

    public abstract String getExtensionCode();

    public abstract SystemExtensionEnum.ExtensionTypeEnum getExtensionType();

    public abstract String getAvatar();

    public abstract String getName();

    public abstract String getVersion();

    public abstract String getAuthor();

    public abstract String getDescription();

    public String getChangelog() {
        return "";
    }

    public String getMinServerVersion() {
        return "";
    }

    protected static String loadClasspathUtf8(Class<?> anchor, String absoluteClasspathPath) {
        try (InputStream in = anchor.getResourceAsStream(absoluteClasspathPath)) {
            if (in == null) {
                return "";
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    protected static String getSafeProperty(Properties properties, String key, String defaultValue) {
        if (properties == null || key == null) {
            return defaultValue;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return defaultValue;
        }
        if (trimmed.startsWith("@") && trimmed.endsWith("@") && trimmed.length() > 2) {
            return defaultValue;
        }
        return trimmed;
    }
}