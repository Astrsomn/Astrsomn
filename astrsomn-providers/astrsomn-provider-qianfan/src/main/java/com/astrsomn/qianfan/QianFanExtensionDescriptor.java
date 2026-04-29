package com.astrsomn.qianfan;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import com.astrsomn.core.common.constant.SystemExtensionEnum;
import com.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class QianFanExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "Baidu Qianfan Model Provider";
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String AVATAR_SVG =
            loadClasspathUtf8(QianFanExtensionDescriptor.class, "/avatar/wenxin-color.svg");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();
    @Override
    public String getExtensionKey() {
        return "qianfan";
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
        return "千帆大模型平台接入；需配置 apiKey + secretKey（对应 ModelSetting.apiKey / apiSecret）。";
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
        try (InputStream inputStream = QianFanExtensionDescriptor.class.getResourceAsStream("/extension-qianfan.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-qianfan.properties", e);
        }
    }
}
