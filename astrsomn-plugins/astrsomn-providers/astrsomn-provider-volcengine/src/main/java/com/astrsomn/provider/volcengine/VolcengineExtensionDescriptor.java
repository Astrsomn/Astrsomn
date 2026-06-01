package com.astrsomn.provider.volcengine;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.system.constant.SystemExtensionEnum;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class VolcengineExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "Volcengine Model Provider";
    private static final String DEFAULT_VERSION = "0.2.0-SNAPSHOT";
    private static final String DEFAULT_CHANGELOG = "";
    private static final String DEFAULT_MIN_SERVER_VERSION = "";
    private static final String AVATAR_BASE64 =
            loadClasspathUtf8(VolcengineExtensionDescriptor.class, "/avatar/volcengine-avatar.base64");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    private static Properties loadExtensionProperties() {
        try (InputStream inputStream = VolcengineExtensionDescriptor.class.getResourceAsStream("/extension-volcengine.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-volcengine.properties", e);
        }
    }

    @Override
    public String getExtensionKey() {
        return AiModelEnum.ProviderEnum.VOLCENGINE.getCode();
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
    public String getName() {
        return getSafeProperty(EXTENSION_PROPERTIES, "name", DEFAULT_NAME);
    }

    @Override
    public String getVersion() {
        return getSafeProperty(EXTENSION_PROPERTIES, "version", DEFAULT_VERSION);
    }

    @Override
    public String getAuthor() {
        return "Astrsomn";
    }

    @Override
    public String getDescription() {
        return "Volcengine model provider — Doubao series chat models via OpenAI-compatible API.";
    }

    @Override
    public String getChangelog() {
        return getSafeProperty(EXTENSION_PROPERTIES, "changelog", DEFAULT_CHANGELOG);
    }

    @Override
    public String getMinServerVersion() {
        return getSafeProperty(EXTENSION_PROPERTIES, "minServerVersion", DEFAULT_MIN_SERVER_VERSION);
    }

    @Override
    public String getAvatar() {
        return AVATAR_BASE64;
    }
}
