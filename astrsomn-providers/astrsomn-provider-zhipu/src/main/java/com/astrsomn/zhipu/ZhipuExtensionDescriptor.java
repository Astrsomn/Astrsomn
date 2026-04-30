package com.astrsomn.zhipu;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import com.astrsomn.core.common.constant.SystemExtensionEnum;
import com.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;

public class ZhipuExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = "Zhipu GLM Model Provider";
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String AVATAR_BASE64 =
            loadClasspathUtf8(ZhipuExtensionDescriptor.class, "/avatar/chatglm-avatar.base64");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    @Override
    public String getExtensionKey() {
        return "zhipu";
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
        return "Zhipu AI";
    }

    @Override
    public String getDescription() {
        return "智谱 GLM 对话与向量模型；参数与模型列表可按业务扩展。";
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
        try (InputStream inputStream = ZhipuExtensionDescriptor.class.getResourceAsStream("/extension-zhipu.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-zhipu.properties", e);
        }
    }
}
