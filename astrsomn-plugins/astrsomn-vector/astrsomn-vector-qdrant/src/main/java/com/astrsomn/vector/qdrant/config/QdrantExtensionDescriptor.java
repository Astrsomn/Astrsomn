package com.astrsomn.vector.qdrant.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import com.astrsomn.api.runtime.common.constant.AiVecDriverEnum;
import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;

public class QdrantExtensionDescriptor extends AstroExtensionDescriptor {

    private static final String DEFAULT_NAME = AiVecDriverEnum.Provider.QDRANT.getDesc();
    private static final String DEFAULT_VERSION = "0.1.0-alpha.1";
    private static final String AVATAR_BASE64 =
            loadClasspathUtf8(QdrantExtensionDescriptor.class, "/avatar/qdrant-avatar.base64");
    private static final Properties EXTENSION_PROPERTIES = loadExtensionProperties();

    @Override
    public String getExtensionKey() {
        return AiVecDriverEnum.Provider.QDRANT.getCode();
    }

    @Override
    public String getExtensionCode() {
        return getExtensionKey();
    }

    @Override
    public SystemExtensionEnum.ExtensionTypeEnum getExtensionType() {
        return SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE;
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
        return "Astrsomn";
    }

    @Override
    public String getDescription() {
        return "Qdrant vector database integration (LangChain4j).";
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
        try (InputStream inputStream = QdrantExtensionDescriptor.class.getResourceAsStream("/extension-qdrant.properties")) {
            if (inputStream == null) {
                return new Properties();
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load /extension-qdrant.properties", e);
        }
    }
}
