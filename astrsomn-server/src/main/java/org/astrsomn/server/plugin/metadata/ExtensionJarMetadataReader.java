package org.astrsomn.server.plugin.metadata;

import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.dto.extension.SystemExtensionMetaData;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.langchain.extension.model.ModelProviderHandler;
import org.astrsomn.core.common.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.StreamSupport;

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

        try (URLClassLoader cl = new URLClassLoader(new URL[]{jar.toURI().toURL()}, parentCl)) {
            return findFirstSpi(AstroExtensionDescriptor.class, cl, jar.getName())
                    .map(d -> {
                        String providerCode = findFirstSpi(ModelProviderHandler.class, cl, jar.getName())
                                .map(ExtensionJarMetadataReader::extractProviderCode)
                                .orElse(null);
                        return buildMetaData(d, providerCode);
                    });
        } catch (Throwable t) {
            log.warn("{} 解析扩展元数据失败 | Jar: {} | 异常: {}", LOG_PREFIX, jar.getName(), t.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 通用的 SPI 获取方法，获取第一个实现并针对多实现打印警告
     */
    private static <T> Optional<T> findFirstSpi(Class<T> serviceClass, ClassLoader cl, String jarName) {
        var instances = StreamSupport.stream(ServiceLoader.load(serviceClass, cl).spliterator(), false).toList();
        if (instances.isEmpty()) return Optional.empty();
        if (instances.size() > 1) {
            log.warn("{} Jar 存在多个 {} 实现，仅取首个 | Jar: {}", LOG_PREFIX, serviceClass.getSimpleName(), jarName);
        }
        return Optional.of(instances.get(0));
    }

    private static String extractProviderCode(ModelProviderHandler handler) {
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
    private static SystemExtensionMetaData buildMetaData(AstroExtensionDescriptor d, String providerCode) {
        return new SystemExtensionMetaData(
                StringUtils.trimToNull(d.getExtensionKey()),
                StringUtils.trimToNull(d.getName()),
                Optional.ofNullable(d.getExtensionType()).map(t -> t.getCode()).orElse(null),
                StringUtils.trimToNull(d.getVersion()),
                StringUtils.trimToNull(d.getAuthor()),
                StringUtils.trimToNull(d.getDescription()),
                StringUtils.trimToNull(d.getAvatar()),
                StringUtils.trimToNull(providerCode)
        );
    }
}