package org.astrsomn.qwen;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

/**
 * 扩展元数据：通过 Java SPI(ServiceLoader) 被系统注册。
 */
public class QwenExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "Qwen Model Provider";
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String AVATAR_SVG =
            loadClasspathUtf8(QwenExtensionDescriptor.class, "/avatar/qwen-color.svg");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    @Override
    public String getExtensionKey() {
        return AiModelEnum.ProviderEnum.ALIBABA.getCode();
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
    }

    @Override
    public String getAvatar() {
        return AVATAR_SVG;
    }

    @Override
    public String getName() {
        return EXTENSION_PROPERTIES.getProperty("name", DEFAULT_NAME);
    }

    @Override
    public String getVersion() {
        return EXTENSION_PROPERTIES.getProperty("version", DEFAULT_VERSION);
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
        try (InputStream inputStream = QwenExtensionDescriptor.class.getResourceAsStream("/extension.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension.properties", e);
        }
    }
}
