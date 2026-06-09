package com.astrsomn.server.service.system.extension.base.impl;

import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.event.SystemMessageEventCoordinator;
import com.astrsomn.server.mapper.SystemExtensionMapper;
import com.astrsomn.server.plugin.metadata.ExtensionJarMetadataReader;
import com.astrsomn.server.plugin.registry.PluginDirectoryExtensionSyncService;
import com.astrsomn.server.plugin.registry.SystemExtensionRegistry;
import com.astrsomn.server.service.system.extension.base.SystemExtensionService;
import com.astrsomn.server.service.system.extension.lifecycle.SystemExtensionLifecycleOrchestrator;
import com.astrsomn.server.util.ExtensionJarUtil;
import com.astrsomn.starter.runtime.context.EnvScope;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.system.constant.SystemMessageEnum;
import com.astrsomn.system.dto.extension.*;
import com.astrsomn.system.dto.systemmessage.SystemMessageRecordCommand;
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
    private final SystemMessageEventCoordinator systemMessageEventCoordinator;

    
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
        // 先取出扩展信息，失败时用于记录失败消息；卸载期间删除会拿不到，所以这里先快照。
        SystemExtensionEntity snapshot = getById(id);
        if (Objects.isNull(snapshot)) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_NOT_FOUND);
        }
        try {
            BaseResponse<String> resp = lifecycleOrchestrator.uninstall(id);
            if (resp != null && resp.isSuccess()) {
                recordExtensionMessage(snapshot,
                        SystemMessageEnum.MessageTypeEnum.PLUGIN_UNINSTALLED,
                        SystemMessageEnum.MessageLevelEnum.SUCCESS,
                        null);
            }
            return resp;
        } catch (BusinessException be) {
            recordExtensionMessage(snapshot,
                    SystemMessageEnum.MessageTypeEnum.PLUGIN_UNINSTALL_FAILED,
                    SystemMessageEnum.MessageLevelEnum.ERROR,
                    String.valueOf(be.getCode()));
            throw be;
        } catch (Exception e) {
            recordExtensionMessage(snapshot,
                    SystemMessageEnum.MessageTypeEnum.PLUGIN_UNINSTALL_FAILED,
                    SystemMessageEnum.MessageLevelEnum.ERROR,
                    String.valueOf(SystemExtensionErrorEnum.EXTENSION_UNINSTALL_FAILED.getCode()));
            throw e;
        }
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

            // 记录系统消息 + 推送前端通知
            recordExtensionMessage(entity,
                    SystemMessageEnum.MessageTypeEnum.PLUGIN_INSTALLED,
                    SystemMessageEnum.MessageLevelEnum.SUCCESS,
                    null);

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

    /**
     * 将插件生命周期事件落库 system_message，并经由 SSE 推送给前端。
     * 通知失败不影响主流程。
     */
    private void recordExtensionMessage(SystemExtensionEntity entity,
                                        SystemMessageEnum.MessageTypeEnum type,
                                        SystemMessageEnum.MessageLevelEnum level,
                                        String errorCode) {
        if (Objects.isNull(entity) || Objects.isNull(type) || Objects.isNull(level)) {
            return;
        }
        try {
            String name = StringUtils.trimToNull(entity.getExtensionName());
            if (name == null) {
                name = StringUtils.trimToNull(entity.getExtensionKey());
            }
            if (name == null) {
                name = "未知插件";
            }
            String version = StringUtils.trimToNull(entity.getVersion());
            String display = version != null ? name + " v" + version : name;

            String title;
            String content;
            if (type == SystemMessageEnum.MessageTypeEnum.PLUGIN_INSTALLED) {
                title = "插件已安装：" + display;
                content = "插件 " + display + " 已上传并注册成功，状态为「已安装」";
            } else if (type == SystemMessageEnum.MessageTypeEnum.PLUGIN_UNINSTALLED) {
                title = "插件已卸载：" + display;
                content = "插件 " + display + " 已从插件目录移除并删除记录";
            } else if (type == SystemMessageEnum.MessageTypeEnum.PLUGIN_INSTALL_FAILED) {
                title = "插件安装失败：" + display;
                content = "插件 " + display + " 安装失败";
            } else {
                title = display;
                content = display;
            }

            SystemMessageRecordCommand cmd = new SystemMessageRecordCommand();
            cmd.setMessageType(type.getCode());
            cmd.setMessageLevel(level.getCode());
            cmd.setReadStatus(SystemMessageEnum.ReadStatusEnum.UNREAD.getCode());
            cmd.setTitle(title);
            cmd.setContent(content);
            cmd.setRefType(SystemMessageEnum.RefTypeEnum.EXTENSION.getCode());
            cmd.setRefId(entity.getId());
            cmd.setRefKey(entity.getExtensionKey());
            cmd.setSource("plugin-marketplace");
            cmd.setErrorCode(errorCode);
            cmd.setEnvCode(EnvScope.get());

            systemMessageEventCoordinator.recordAndPush(cmd);
        } catch (Exception ex) {
            // 仅记录日志，不影响主业务
            log.warn("记录插件系统消息失败: type={}, key={}, err={}",
                    type, entity.getExtensionKey(), ex.getMessage());
        }
    }

}
