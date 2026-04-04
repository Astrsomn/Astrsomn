package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.extension.SystemExtensionCreateRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionQueryRequestDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionResponseDTO;
import org.astrsomn.core.common.dto.extension.SystemExtensionUpdateRequestDTO;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.astrsomn.server.plugin.ExtensionJarMetadata;
import org.astrsomn.server.plugin.ExtensionJarMetadataReader;
import org.astrsomn.server.plugin.SystemExtensionRegistry;
import org.astrsomn.server.service.SystemExtensionService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.server.service.support.SystemExtensionModelGuard;
import org.astrsomn.starter.plugin.AstrsomnPluginManager;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemExtensionServiceImpl extends ServiceImpl<SystemExtensionMapper, SystemExtensionEntity>
        implements SystemExtensionService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionModelGuard systemExtensionModelGuard;
    private final ApplicationContext applicationContext;

    @Override
    public BaseResponse<String> create(SystemExtensionCreateRequestDTO request) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus("INSTALLED");
        }
        if (StringUtils.isBlank(entity.getApplied())) {
            entity.setApplied("N");
        }
        boolean result = save(entity);
        return result ? BaseResponse.success("安装成功，待应用") : BaseResponse.fail("安装失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<SystemExtensionResponseDTO> detail(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        SystemExtensionResponseDTO responseDTO = new SystemExtensionResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        fillAvatarFromDescriptors(responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemExtensionUpdateRequestDTO request) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request) {
        IPage<SystemExtensionResponseDTO> page = request.buildPage();
        SystemExtensionQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new SystemExtensionQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<SystemExtensionResponseDTO> result = baseMapper.queryPage(page, param);
        Map<String, AstroExtensionDescriptor> descriptorsByKey =
                SystemExtensionRegistry.mergeDescriptors(applicationContext);
        if (result.getRecords() != null) {
            for (SystemExtensionResponseDTO row : result.getRecords()) {
                fillAvatarFromDescriptors(row, descriptorsByKey);
            }
        }
        return PageResponse.buildResponse(result);
    }

    private void fillAvatarFromDescriptors(SystemExtensionResponseDTO dto) {
        fillAvatarFromDescriptors(dto, SystemExtensionRegistry.mergeDescriptors(applicationContext));
    }

    /**
     * 库中 AVATAR 为空时，按 extensionKey 用内置 {@link AstroExtensionDescriptor} 补全（已安装列表/详情与 SPI 展示一致）。
     */
    private static void fillAvatarFromDescriptors(
            SystemExtensionResponseDTO dto, Map<String, AstroExtensionDescriptor> descriptorsByKey) {
        if (dto == null || StringUtils.isNotBlank(dto.getAvatar())) {
            return;
        }
        String key = StringUtils.trimToNull(dto.getExtensionKey());
        if (key == null) {
            return;
        }
        AstroExtensionDescriptor d = descriptorsByKey.get(key);
        if (d == null) {
            return;
        }
        String svg = d.getAvatar();
        if (StringUtils.isNotBlank(svg)) {
            dto.setAvatar(svg);
        }
    }

    @Override
    public BaseResponse<String> apply(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (StringUtils.isBlank(entity.getJarName())) {
            return BaseResponse.fail("未配置 jarName，无法应用插件", null);
        }
        try {
            pluginManager.applyPlugin(entity.getJarName());
            entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.Y.getCode());
            entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.APPLIED.getCode());
            updateById(entity);
            return BaseResponse.success("应用成功");
        } catch (Exception e) {
            return BaseResponse.fail("应用失败: " + e.getMessage(), null);
        }
    }

    @Override
    public BaseResponse<String> revokeApply(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionInstallStatusEnum.APPLIED.getCode().equals(entity.getStatus())) {
            return BaseResponse.fail("当前不是已应用状态，无需取消应用", null);
        }
        if (SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(entity.getType())) {
            BaseResponse<Void> guard = systemExtensionModelGuard.assertNoInstancesUseProviderModels(id);
            if (!guard.isSuccess()) {
                return BaseResponse.fail(guard.getMessage(), null);
            }
        }
        if (StringUtils.isNotBlank(entity.getJarName())) {
            pluginManager.unloadPlugin(entity.getJarName());
        }
        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        boolean result = updateById(entity);
        return result ? BaseResponse.success("已恢复为已安装") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public BaseResponse<String> uninstall(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(entity.getType())) {
            BaseResponse<Void> guard = systemExtensionModelGuard.assertNoInstancesUseProviderModels(id);
            if (!guard.isSuccess()) {
                return BaseResponse.fail(guard.getMessage(), null);
            }
        }
        if (StringUtils.isNotBlank(entity.getJarName())) {
            pluginManager.unloadPlugin(entity.getJarName());
        }
        entity.setApplied("N");
        entity.setStatus("UNINSTALLED");
        boolean result = updateById(entity);
        return result ? BaseResponse.success("卸载成功") : BaseResponse.fail("卸载失败", null);
    }

    @Override
    public BaseResponse<String> uploadJar(
            MultipartFile file,
            String extensionKey,
            String extensionName,
            String type,
            String version,
            String author,
            String description,
            String providerCode) {
        if (file == null || file.isEmpty()) {
            return BaseResponse.fail("请选择 jar 文件", null);
        }
        String originalName = file.getOriginalFilename();
        String safeJarName;
        try {
            safeJarName = sanitizeJarFileName(originalName);
        } catch (IllegalArgumentException e) {
            return BaseResponse.fail(e.getMessage(), null);
        }

        File pluginsDir = pluginManager.getPluginsDirectory();
        File dest = new File(pluginsDir, safeJarName);
        if (dest.exists()) {
            return BaseResponse.fail("plugins 目录下已存在同名文件: " + safeJarName, null);
        }

        String stem = safeJarName.substring(0, safeJarName.length() - 4);

        File tempJar = null;
        try {
            tempJar = File.createTempFile("astro-ext-upload-", ".jar");
            file.transferTo(tempJar.toPath());
        } catch (IOException e) {
            log.error("暂存上传 jar 失败", e);
            return BaseResponse.fail("保存文件失败: " + e.getMessage(), null);
        }

        Optional<ExtensionJarMetadata> jarMeta = ExtensionJarMetadataReader.tryLoad(tempJar);

        String key;
        try {
            key = resolveExtensionKeyForUpload(StringUtils.trimToNull(extensionKey), jarMeta, stem);
        } catch (IllegalArgumentException e) {
            try {
                Files.deleteIfExists(tempJar.toPath());
            } catch (IOException ignored) {
                // ignore
            }
            return BaseResponse.fail(e.getMessage(), null);
        }
        if (StringUtils.isBlank(key)) {
            try {
                Files.deleteIfExists(tempJar.toPath());
            } catch (IOException ignored) {
                // ignore
            }
            return BaseResponse.fail("extensionKey 无效", null);
        }

        Long dup = baseMapper.selectCount(
                new LambdaQueryWrapper<SystemExtensionEntity>().eq(SystemExtensionEntity::getExtensionKey, key));
        if (dup != null && dup > 0) {
            try {
                Files.deleteIfExists(tempJar.toPath());
            } catch (IOException ignored) {
                // ignore
            }
            return BaseResponse.fail("扩展 Key 已存在: " + key, null);
        }

        try {
            Files.createDirectories(pluginsDir.toPath());
            Files.move(tempJar.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tempJar = null;
        } catch (IOException e) {
            log.error("移动插件 jar 到 plugins 失败: {}", safeJarName, e);
            if (tempJar != null) {
                try {
                    Files.deleteIfExists(tempJar.toPath());
                } catch (IOException ignored) {
                    // ignore
                }
            }
            return BaseResponse.fail("保存文件失败: " + e.getMessage(), null);
        }

        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(key);
        entity.setJarName(safeJarName);
        entity.setExtensionName(pickMeta(extensionName, jarMeta.map(ExtensionJarMetadata::extensionName)));
        entity.setType(pickMeta(type, jarMeta.map(ExtensionJarMetadata::type)));
        entity.setVersion(pickMeta(version, jarMeta.map(ExtensionJarMetadata::version)));
        entity.setAuthor(pickMeta(author, jarMeta.map(ExtensionJarMetadata::author)));
        entity.setDescription(pickMeta(description, jarMeta.map(ExtensionJarMetadata::description)));
        entity.setProviderCode(pickMeta(providerCode, jarMeta.map(ExtensionJarMetadata::providerCode)));
        entity.setAvatar(pickMeta(null, jarMeta.map(ExtensionJarMetadata::avatar)));

        applyManifestDefaults(dest, entity);

        if (StringUtils.isBlank(entity.getExtensionName())) {
            entity.setExtensionName(stem);
        }
        if (StringUtils.isBlank(entity.getType())) {
            entity.setType(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode());
        }
        if (StringUtils.isBlank(entity.getVersion())) {
            entity.setVersion("unknown");
        }
        if (StringUtils.isBlank(entity.getAuthor())) {
            entity.setAuthor("unknown");
        }

        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());

        boolean saved = save(entity);
        if (!saved) {
            try {
                java.nio.file.Files.deleteIfExists(dest.toPath());
            } catch (IOException ex) {
                log.warn("回滚删除 jar 失败: {}", dest.getAbsolutePath(), ex);
            }
            return BaseResponse.fail("登记扩展记录失败", null);
        }

        try {
            pluginManager.reloadPlugins();
        } catch (Exception e) {
            log.warn("上传后 reloadPlugins 异常（文件已保存、记录已写入）: {}", e.getMessage());
        }

        return BaseResponse.success("已上传到 plugins 并登记为已安装，请到「已安装插件」中应用。");
    }

    private static String sanitizeJarFileName(String original) {
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

    private static String sanitizeExtensionKey(String raw) {
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

    /** 由 jar 文件名推导 Key：非 [a-zA-Z0-9._-] 替换为下划线，保证以字母或数字开头。 */
    private static String defaultExtensionKeyFromStem(String stem) {
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
     * 请求显式传入的 extensionKey 优先；否则用 jar 内 {@link AstroExtensionDescriptor#getExtensionKey()}；再否则由文件名推导。
     */
    private String resolveExtensionKeyForUpload(
            String paramKey, Optional<ExtensionJarMetadata> jarMeta, String stem) {
        if (StringUtils.isNotBlank(paramKey)) {
            return sanitizeExtensionKey(paramKey);
        }
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

    /** 表单字段非空优先，否则使用 jar 内解析值。 */
    private static String pickMeta(String requestOverride, Optional<String> fromJar) {
        String r = StringUtils.trimToNull(requestOverride);
        if (r != null) {
            return r;
        }
        return fromJar.filter(StringUtils::isNotBlank).orElse(null);
    }

    private static void applyManifestDefaults(File jarFile, SystemExtensionEntity entity) {
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

    private static String firstNonBlank(String a, String b) {
        if (StringUtils.isNotBlank(a)) {
            return a.trim();
        }
        if (StringUtils.isNotBlank(b)) {
            return b.trim();
        }
        return null;
    }
}
