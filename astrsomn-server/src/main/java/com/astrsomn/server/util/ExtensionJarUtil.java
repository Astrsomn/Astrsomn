package com.astrsomn.server.util;

import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionMetaData;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@Slf4j
public class ExtensionJarUtil {

    /**
     * 清理jar文件名，确保文件名合法且以.jar结尾
     */
    public static String sanitizeJarFileName(String original) {
        if (StringUtils.isBlank(original)) {
            throw new IllegalArgumentException("文件名无效");
        }
        String name = new File(original).getName();
        if (name.contains("..") || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0) {
            throw new IllegalArgumentException("非法文件名");
        }
        if (!name.toLowerCase(Locale.ROOT).endsWith(".jar")) {
            throw new IllegalArgumentException("仅支持 .jar 文件");
        }
        return name;
    }

    /**
     * 清理扩展key，确保格式合法
     */
    public static String sanitizeExtensionKey(String raw) {
        if (raw == null) {
            return null;
        }
        String t = raw.trim();
        if (t.isEmpty()) {
            return null;
        }
        if (!t.matches("[a-zA-Z0-9][a-zA-Z0-9._-]*")) {
            throw new IllegalArgumentException("extensionKey 仅允许字母、数字、点、下划线、中划线，且不能以点开头");
        }
        return t;
    }

    /**
     * 由 jar 文件名推导 Key：非 [a-zA-Z0-9._-] 替换为下划线，保证以字母或数字开头
     */
    public static String defaultExtensionKeyFromStem(String stem) {
        if (StringUtils.isBlank(stem)) {
            return "jar_" + System.currentTimeMillis();
        }
        String n = stem.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
        n = n.replaceAll("_+", "_");
        n = n.replaceAll("^[._-]+", "");
        n = n.replaceAll("[._-]+$", "");
        if (n.isEmpty() || !Character.isLetterOrDigit(n.charAt(0))) {
            n = "ext_" + System.currentTimeMillis();
        }
        return n;
    }

    /**
     * 上传接口只接收文件：优先用 jar 内
     * {@link AstroExtensionDescriptor#getExtensionKey()}，
     * 缺失时由文件名推导。
     */
    public static String resolveExtensionKeyForUpload(Optional<SystemExtensionMetaData> jarMeta, String stem) {
        if (jarMeta.isPresent()) {
            String k = StringUtils.trimToNull(jarMeta.get().extensionKey());
            if (k != null) {
                try {
                    return sanitizeExtensionKey(k);
                } catch (IllegalArgumentException e) {
                    log.warn("jar 内 extensionKey 不合法，改用文件名推导: {} — {}", k, e.getMessage());
                }
            }
        }
        return defaultExtensionKeyFromStem(stem);
    }

    /**
     * 表单字段非空优先，否则使用 jar 内解析值
     */
    public static String pickMeta(String requestOverride, Optional<String> fromJar) {
        String r = StringUtils.trimToNull(requestOverride);
        if (r != null) {
            return r;
        }
        return fromJar.filter(StringUtils::isNotBlank).orElse(null);
    }

    /**
     * 应用manifest默认值到实体
     */
    public static void applyManifestDefaults(File jarFile, SystemExtensionEntity entity) {
        try (JarFile jf = new JarFile(jarFile)) {
            Manifest mf = jf.getManifest();
            if (mf == null) {
                return;
            }
            Attributes main = mf.getMainAttributes();
            if (main == null) {
                return;
            }
            if (StringUtils.isBlank(entity.getExtensionName())) {
                String title = firstNonBlank(
                        main.getValue(Attributes.Name.IMPLEMENTATION_TITLE),
                        main.getValue("Bundle-Name"));
                entity.setExtensionName(StringUtils.trimToNull(title));
            }
            if (StringUtils.isBlank(entity.getVersion())) {
                String ver = firstNonBlank(
                        main.getValue(Attributes.Name.IMPLEMENTATION_VERSION),
                        main.getValue("Bundle-Version"));
                entity.setVersion(StringUtils.trimToNull(ver));
            }
            if (StringUtils.isBlank(entity.getAuthor())) {
                entity.setAuthor(StringUtils.trimToNull(main.getValue(Attributes.Name.IMPLEMENTATION_VENDOR)));
            }
        } catch (IOException e) {
            log.debug("读取 manifest 跳过: {}", jarFile.getName(), e);
        }
    }

    /**
     * 获取第一个非空值
     */
    public static String firstNonBlank(String a, String b) {
        if (StringUtils.isNotBlank(a)) {
            return a.trim();
        }
        if (StringUtils.isNotBlank(b)) {
            return b.trim();
        }
        return null;
    }
}
