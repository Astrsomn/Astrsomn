package com.astrsomn.provider.qwen;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import com.astrsomn.api.runtime.common.constant.AiModelEnum;
import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;

/**
 * 扩展元数据：通过 Java SPI(ServiceLoader) 被系统注册。
 */
public class QwenExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "Qwen Model Provider";
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String AVATAR_BASE64 =
            loadClasspathUtf8(QwenExtensionDescriptor.class, "/avatar/qwen-avatar.base64");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

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

    private static Properties loadExtensionProperties() {
        try (InputStream inputStream = QwenExtensionDescriptor.class.getResourceAsStream("/extension-qwen.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-qwen.properties", e);
        }
    }
}
