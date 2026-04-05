package org.astrsomn.server.plugin;

import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.dto.extension.SystemExtensionMetaData;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.langchain.extension.ModelProviderHandler;
import org.astrsomn.core.common.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * 使用独立 {@link URLClassLoader} 读取 jar 内 SPI，避免与当前进程已加载的同名类冲突；读取后关闭加载器。
 */
@Slf4j
public final class ExtensionJarMetadataReader {

    private ExtensionJarMetadataReader() {}

    public static Optional<SystemExtensionMetaData> tryLoad(File jar) {
        if (jar == null || !jar.isFile()) {
            return Optional.empty();
        }
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ExtensionJarMetadataReader.class.getClassLoader();
        }
        try (URLClassLoader cl = new URLClassLoader(new URL[] { jar.toURI().toURL() }, parent)) {
            List<AstroExtensionDescriptor> descriptors = new ArrayList<>();
            ServiceLoader.load(AstroExtensionDescriptor.class, cl).forEach(descriptors::add);
            if (descriptors.isEmpty()) {
                return Optional.empty();
            }
            if (descriptors.size() > 1) {
                log.warn("jar 中存在多个 AstroExtensionDescriptor SPI，仅使用第一个: {}", jar.getName());
            }
            AstroExtensionDescriptor d = descriptors.get(0);

            String providerCode = null;
            List<ModelProviderHandler> handlers = new ArrayList<>();
            ServiceLoader.load(ModelProviderHandler.class, cl).forEach(handlers::add);
            if (handlers.size() > 1) {
                log.warn("jar 中存在多个 ModelProviderHandler SPI，仅使用第一个取 providerCode: {}", jar.getName());
            }
            if (!handlers.isEmpty()) {
                try {
                    providerCode = handlers.get(0).getProvider().getCode();
                } catch (Exception e) {
                    log.debug("读取 ModelProviderHandler 厂商 code 跳过: {}", e.getMessage());
                }
            }

            String avatar = d.getAvatar();
            return Optional.of(
                    new SystemExtensionMetaData(
                            StringUtils.trimToNull(d.getExtensionKey()),
                            StringUtils.trimToNull(d.getName()),
                            d.getExtensionType() != null ? d.getExtensionType().getCode() : null,
                            StringUtils.trimToNull(d.getVersion()),
                            StringUtils.trimToNull(d.getAuthor()),
                            StringUtils.trimToNull(d.getDescription()),
                            StringUtils.trimToNull(avatar),
                            StringUtils.trimToNull(providerCode)));
        } catch (Throwable t) {
            log.warn("从 jar 解析扩展元数据失败（将回退为文件名/Manifest）: {} — {}", jar.getName(), t.getMessage());
            return Optional.empty();
        }
    }
}
