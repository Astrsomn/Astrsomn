package com.astrsomn.provider.openai;

import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class OpenAiExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "OpenAI Model Provider";
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String AVATAR_BASE64 =
            loadClasspathUtf8(OpenAiExtensionDescriptor.class, "/avatar/openai-avatar.base64");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    private static Properties loadExtensionProperties() {
        try (InputStream inputStream = OpenAiExtensionDescriptor.class.getResourceAsStream("/extension-openai.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-openai.properties", e);
        }
    }

    @Override
    public String getExtensionKey() {
        return "openai";
    }

    @Override
    public String getExtensionCode() {
        return getExtensionKey();
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getAvatar() {
        return AVATAR_BASE64;
    }

    @Override
    public String getName() {
        return getSafeProperty(EXTENSION_PROPERTIES, "name", DEFAULT_NAME);
    }

    @Override
    public String getVersion() {
        return getSafeProperty(EXTENSION_PROPERTIES, "version", DEFAULT_VERSION);
    }

    @Override
    public String getAuthor() {
        return "";
    }

    @Override
    public String getDescription() {
        return "OpenAI 及 OpenAI 兼容 API；可在本模块扩展 Image 等能力。";
    }

    @Override
    public String getChangelog() {
        return EXTENSION_PROPERTIES.getProperty("changelog", "");
    }

    @Override
    public String getMinServerVersion() {
        return EXTENSION_PROPERTIES.getProperty("minServerVersion", "");
    }
}
