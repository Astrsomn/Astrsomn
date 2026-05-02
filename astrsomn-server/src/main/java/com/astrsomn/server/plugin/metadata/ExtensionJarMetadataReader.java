package com.astrsomn.server.plugin.metadata;

import lombok.extern.slf4j.Slf4j;
import com.astrsomn.api.runtime.common.dto.extension.SystemExtensionMetaData;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.api.runtime.common.langchain.extension.model.ModelProviderHandler;
import com.astrsomn.common.utils.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarFile;

/**
 * 使用独立 {@link URLClassLoader} 读取 jar 内 SPI，避免与当前进程已加载的同名类冲突；读取后关闭加载器。
 */
@Slf4j
public final class ExtensionJarMetadataReader {

    private static final String LOG_PREFIX = "[Astrsomn] [扩展解析器] ====> ";

    private ExtensionJarMetadataReader() {}

    /**
     * 尝试从 JAR 文件中加载系统扩展元数据
     */
    public static Optional<SystemExtensionMetaData> tryLoad(File jar) {
        if (Objects.isNull(jar) || !jar.isFile()) return Optional.empty();

        ClassLoader parentCl = Optional.ofNullable(Thread.currentThread().getContextClassLoader())
                .orElse(ExtensionJarMetadataReader.class.getClassLoader());

        try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()}, parentCl);
             JarFile jarFile = new JarFile(jar)) {
            return findFirstSpiInJar(AstroExtensionDescriptor.class, cl, jarFile, jar.getName())
                    .map(d -> {
                        String extensionCode = findFirstSpiInJar(ModelProviderHandler.class, cl, jarFile, jar.getName())
                                .map(ExtensionJarMetadataReader::extractExtensionCode)
                                .orElse(null);
                        return buildMetaData(d, extensionCode);
                    });
        } catch (Throwable t) {
            log.warn("{} 解析扩展元数据失败 | Jar: {} | 异常: {}", LOG_PREFIX, jar.getName(), t.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 仅从目标 jar 的 META-INF/services 读取 SPI，避免父 ClassLoader 里的 SPI 干扰。
     */
    private static <T> Optional<T> findFirstSpiInJar(Class<T> serviceClass, ClassLoader cl, JarFile jarFile, String jarName) {
        String spiPath = "META-INF/services/" + serviceClass.getName();
        var entry = jarFile.getJarEntry(spiPath);
        if (entry == null) {
            return Optional.empty();
        }
        List<String> implClassNames = readServiceImplClassNames(jarFile, entry);
        if (implClassNames.isEmpty()) {
            return Optional.empty();
        }
        if (implClassNames.size() > 1) {
            log.warn("{} Jar 存在多个 {} 实现，仅取首个 | Jar: {}", LOG_PREFIX, serviceClass.getSimpleName(), jarName);
        }
        String className = implClassNames.get(0);
        try {
            Class<?> implClass = Class.forName(className, true, cl);
            if (!serviceClass.isAssignableFrom(implClass)) {
                log.warn("{} SPI 实现类型不匹配 | Service: {} | Impl: {} | Jar: {}",
                        LOG_PREFIX, serviceClass.getSimpleName(), className, jarName);
                return Optional.empty();
            }
            Object instance = implClass.getDeclaredConstructor().newInstance();
            return Optional.of(serviceClass.cast(instance));
        } catch (Throwable t) {
            log.warn("{} SPI 实现实例化失败 | Service: {} | Impl: {} | Jar: {} | 异常: {}",
                    LOG_PREFIX, serviceClass.getSimpleName(), className, jarName, t.getMessage());
            return Optional.empty();
        }
    }

    private static List<String> readServiceImplClassNames(JarFile jarFile, java.util.jar.JarEntry entry) {
        try (InputStream in = jarFile.getInputStream(entry)) {
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<String> classNames = new ArrayList<>();
            for (String line : content.split("\\R")) {
                String trimmed = StringUtils.trimToNull(line);
                if (trimmed == null || trimmed.startsWith("#")) {
                    continue;
                }
                int commentPos = trimmed.indexOf('#');
                if (commentPos >= 0) {
                    trimmed = StringUtils.trimToNull(trimmed.substring(0, commentPos));
                }
                if (trimmed != null) {
                    classNames.add(trimmed);
                }
            }
            return classNames;
        } catch (Exception e) {
            return List.of();
        }
    }

    private static String extractExtensionCode(ModelProviderHandler handler) {
        try {
            return handler.getProvider().getCode();
        } catch (Exception e) {
            log.debug("{} 读取厂商 Code 跳过: {}", LOG_PREFIX, e.getMessage());
            return null;
        }
    }

    /**
     * 清洗数据并构建元数据实体
     */
    private static SystemExtensionMetaData buildMetaData(AstroExtensionDescriptor d, String extensionCode) {
        return new SystemExtensionMetaData(
                StringUtils.trimToNull(d.getExtensionKey()),
                StringUtils.trimToNull(d.getName()),
                Optional.ofNullable(d.getExtensionType()).map(t -> t.getCode()).orElse(null),
                StringUtils.trimToNull(d.getVersion()),
                StringUtils.trimToNull(d.getAuthor()),
                StringUtils.trimToNull(d.getDescription()),
                StringUtils.trimToNull(d.getAvatar()),
                StringUtils.trimToNull(extensionCode),
                StringUtils.trimToNull(d.getChangelog()),
                StringUtils.trimToNull(d.getMinServerVersion())
        );
    }
}