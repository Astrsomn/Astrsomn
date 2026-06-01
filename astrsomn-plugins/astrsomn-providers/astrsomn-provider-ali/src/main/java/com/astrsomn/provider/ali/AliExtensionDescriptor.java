package com.astrsomn.provider.ali;

import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.system.constant.SystemExtensionEnum;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;


public class AliExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "Qwen Model Provider";
    private static final String DEFAULT_VERSION = "0.2.0-SNAPSHOT";
    private static final String AVATAR_BASE64 =
            loadClasspathUtf8(AliExtensionDescriptor.class, "/avatar/ali-avatar.base64");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    private static Properties loadExtensionProperties() {
        try (InputStream inputStream = AliExtensionDescriptor.class.getResourceAsStream("/extension-ali.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-ali.properties", e);
        }
    }

    @Override
    public String getExtensionKey() {
        return AiModelEnum.ProviderEnum.ALIBABA.getCode();
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
        return "Alibaba Cloud";
    }

    @Override
    public String getDescription() {
        return "Qwen provider extension for chat and embedding models.";
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
