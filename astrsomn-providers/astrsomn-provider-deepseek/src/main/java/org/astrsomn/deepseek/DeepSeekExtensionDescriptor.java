package org.astrsomn.deepseek;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import org.astrsomn.core.common.constant.AiModelEnum;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class DeepSeekExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "DeepSeek Model Provider";
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String DEFAULT_CHANGELOG = "";
    private static final String DEFAULT_MIN_SERVER_VERSION = "";
    private static final String AVATAR_SVG =
            loadClasspathUtf8(DeepSeekExtensionDescriptor.class, "/avatar/deepseek-color.svg");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    @Override
    public String getExtensionKey() {
        return AiModelEnum.ProviderEnum.DEEPSEEK.getCode();
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER;
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
        return "Astrsomn";
    }

    @Override
    public String getDescription() {
        return "DeepSeek provider (OpenAI-compatible API); 模型清单与调用细节可在本模块内扩展。";
    }

    @Override
    public String getChangelog() {
        return EXTENSION_PROPERTIES.getProperty("changelog", DEFAULT_CHANGELOG);
    }

    @Override
    public String getMinServerVersion() {
        return EXTENSION_PROPERTIES.getProperty("minServerVersion", DEFAULT_MIN_SERVER_VERSION);
    }

    @Override
    public String getAvatar() {
        return AVATAR_SVG;
    }

    private static Properties loadExtensionProperties() {
        try (InputStream inputStream = DeepSeekExtensionDescriptor.class.getResourceAsStream("/extension.properties")) {
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
