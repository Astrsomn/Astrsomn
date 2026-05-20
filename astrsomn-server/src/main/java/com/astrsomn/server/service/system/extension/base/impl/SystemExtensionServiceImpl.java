package com.astrsomn.server.service.system.extension.base.impl;

import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.SystemExtensionMapper;
import com.astrsomn.server.plugin.metadata.ExtensionJarMetadataReader;
import com.astrsomn.server.plugin.registry.PluginDirectoryExtensionSyncService;
import com.astrsomn.server.plugin.registry.SystemExtensionRegistry;
import com.astrsomn.server.service.system.extension.base.SystemExtensionService;
import com.astrsomn.server.service.system.extension.lifecycle.SystemExtensionLifecycleOrchestrator;
import com.astrsomn.server.util.ExtensionJarUtil;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.dto.extension.*;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.astrsomn.system.exception.SystemExtensionErrorEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemExtensionServiceImpl extends ServiceImpl<SystemExtensionMapper, SystemExtensionEntity>
        implements SystemExtensionService {

    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionLifecycleOrchestrator lifecycleOrchestrator;
    private final ApplicationContext applicationContext;
    private final PluginDirectoryExtensionSyncService pluginDirectoryExtensionSyncService;

    
    private static void fillAvatarFromDescriptors(
            SystemExtensionResponseDTO dto, Map<String, AstroExtensionDescriptor> descriptorsByKey) {
        if (Objects.isNull(dto) || StringUtils.isNotBlank(dto.getAvatar())) {
            return;
        }
        String key = StringUtils.trimToNull(dto.getExtensionKey());
        if (Objects.isNull(key)) {
            return;
        }
        AstroExtensionDescriptor d = descriptorsByKey.get(key);
        if (Objects.isNull(d)) {
            return;
        }
        String svg = d.getAvatar();
        if (StringUtils.isNotBlank(svg)) {
            dto.setAvatar(svg);
        }
    }

    @Override
    public BaseResponse<String> create(SystemExtensionCreateRequestDTO request) {
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getStatus())) {
            entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        }
        if (StringUtils.isBlank(entity.getApplied())) {
            entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        }
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_CREATE_FAILED);
        }
        return BaseResponse.success("Installed, pending apply");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_DELETE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<SystemExtensionResponseDTO> detail(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (Objects.isNull(entity)) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_NOT_FOUND);
        }
        SystemExtensionResponseDTO responseDTO = new SystemExtensionResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        fillAvatarFromDescriptors(responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemExtensionUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR);
        }
        SystemExtensionEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_NOT_FOUND);
        }
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request) {
        IPage<SystemExtensionResponseDTO> page = PageUtils.buildPage(request);
        SystemExtensionQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new SystemExtensionQueryRequestDTO();
        }
        IPage<SystemExtensionResponseDTO> result = baseMapper.queryPage(page, param);
        Map<String, AstroExtensionDescriptor> descriptorsByKey =
                SystemExtensionRegistry.mergeDescriptors(applicationContext);
        if (Objects.nonNull(result.getRecords())) {
            for (SystemExtensionResponseDTO row : result.getRecords()) {
                fillAvatarFromDescriptors(row, descriptorsByKey);
            }
        }
        return PageConverter.toResponse(result);
    }

    private void fillAvatarFromDescriptors(SystemExtensionResponseDTO dto) {
        fillAvatarFromDescriptors(dto, SystemExtensionRegistry.mergeDescriptors(applicationContext));
    }

    @Override
    public BaseResponse<String> apply(Long id) {
        return lifecycleOrchestrator.apply(id);
    }

    @Override
    public BaseResponse<String> revokeApply(Long id) {
        return lifecycleOrchestrator.revoke(id);
    }

    @Override
    public BaseResponse<String> uninstall(Long id) {
        return lifecycleOrchestrator.uninstall(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> uploadJar(MultipartFile file) {

        // Validate input
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "Please select a jar file");
        }

        String originalName = file.getOriginalFilename();
        String safeJarName = ExtensionJarUtil.sanitizeJarFileName(originalName);

        if (!safeJarName.toLowerCase().endsWith(".jar")) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "Only .jar files are supported");
        }

        File pluginsDir = pluginManager.getPluginsDirectory();
        File destFile = new File(pluginsDir, safeJarName);
        if (destFile.exists()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "File already exists in plugins directory: " + safeJarName);
        }

        String stem = safeJarName.substring(0, safeJarName.length() - 4);
        File tempJar = null;

        try {
            // Stage and parse the uploaded file
            try {
                tempJar = File.createTempFile("astro-ext-upload-", ".jar");
                file.transferTo(tempJar);
            } catch (IOException e) {
                log.error("Failed to save uploaded jar to temp file", e);
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPLOAD_FAILED, "Failed to save temp file");
            }

            Optional<SystemExtensionMetaData> jarMeta = ExtensionJarMetadataReader.tryLoad(tempJar);
            if (jarMeta.isEmpty()) {
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "Extension descriptor not found; ensure the jar contains AstroExtensionDescriptor SPI configuration");
            }

            String key = ExtensionJarUtil.resolveExtensionKeyForUpload(jarMeta, stem);
            if (StringUtils.isBlank(key)) {
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "Extension key is invalid");
            }

            boolean exists = baseMapper.exists(new LambdaQueryWrapper<SystemExtensionEntity>()
                    .eq(SystemExtensionEntity::getExtensionKey, key));
            if (exists) {
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "Extension key already exists: " + key);
            }

            // Move file to target plugins directory
            try {
                if (!pluginsDir.exists()) {
                    Files.createDirectories(pluginsDir.toPath());
                }
                Files.move(tempJar.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error("Failed to move plugin jar: {}", safeJarName, e);
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPLOAD_FAILED, "Failed to move file");
            }

            // Build and persist entity
            SystemExtensionEntity entity = buildExtensionEntity(key, safeJarName, stem, jarMeta.get());

            // Apply manifest defaults from the final file
            ExtensionJarUtil.applyManifestDefaults(destFile, entity);

            if (!save(entity)) {
                FileUtils.deleteQuietly(destFile);
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_CREATE_FAILED, "Failed to register extension record");
            }

            // Refresh plugin container
            refreshPluginSafely();

            return BaseResponse.success("Uploaded and registered successfully");

        } finally {
            if (Objects.nonNull(tempJar) && tempJar.exists()) {
                FileUtils.deleteQuietly(tempJar);
            }
        }
    }

    
    private SystemExtensionEntity buildExtensionEntity(
            String key, String jarName, String stem, SystemExtensionMetaData meta) {

        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(key);
        entity.setJarName(jarName);

        // Extension metadata is read from the jar descriptor
        entity.setExtensionName(StringUtils.trimToNull(meta.extensionName()));
        entity.setType(StringUtils.trimToNull(meta.type()));
        entity.setVersion(StringUtils.trimToNull(meta.version()));
        entity.setAuthor(StringUtils.trimToNull(meta.author()));
        entity.setDescription(StringUtils.trimToNull(meta.description()));
        entity.setExtensionCode(StringUtils.trimToNull(meta.extensionCode()));
        entity.setAvatar(StringUtils.trimToNull(meta.avatar()));
        entity.setChangelog(StringUtils.trimToNull(meta.changelog()));
        entity.setMinServerVersion(StringUtils.trimToNull(meta.minServerVersion()));

        // Fill missing defaults
        if (StringUtils.isBlank(entity.getExtensionName())) entity.setExtensionName(stem);
        if (StringUtils.isBlank(entity.getType()))
            entity.setType(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode());
        if (StringUtils.isBlank(entity.getVersion())) entity.setVersion("1.0.0");
        if (StringUtils.isBlank(entity.getAuthor())) entity.setAuthor("unknown");

        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        // Mark source and discovery mechanism for platform-managed extensions
        entity.setDiscoveryMechanism(SystemExtensionEnum.DiscoveryMechanismEnum.SPI.getCode());
        entity.setInstallSource(SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_UPLOAD.getCode());

        return entity;
    }

    private void refreshPluginSafely() {
        try {
            pluginManager.reloadPlugins();
            pluginDirectoryExtensionSyncService.syncDiscoveredPlugins();
        } catch (Exception e) {
            log.error("Post-upload reloadPlugins failed, retry manually: {}", e.getMessage());
        }
    }

}
