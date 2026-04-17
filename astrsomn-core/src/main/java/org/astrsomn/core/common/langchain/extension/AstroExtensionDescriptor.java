package org.astrsomn.core.common.langchain.extension;

import org.astrsomn.core.common.constant.SystemExtensionEnum;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class AstroExtensionDescriptor {

    public abstract String getExtensionKey();

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
}
