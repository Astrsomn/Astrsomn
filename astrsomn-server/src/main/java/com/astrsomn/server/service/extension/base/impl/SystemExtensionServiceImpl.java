package com.astrsomn.server.service.extension.base.impl;

import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.dto.extension.*;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.api.runtime.common.langchain.extension.AstroExtensionDescriptor;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.SystemExtensionErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.SystemExtensionMapper;
import com.astrsomn.server.plugin.metadata.ExtensionJarMetadataReader;
import com.astrsomn.server.plugin.registry.PluginDirectoryExtensionSyncService;
import com.astrsomn.server.plugin.registry.SystemExtensionRegistry;
import com.astrsomn.server.service.extension.base.SystemExtensionService;
import com.astrsomn.server.service.extension.lifecycle.SystemExtensionLifecycleOrchestrator;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.server.util.ExtensionJarUtil;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemExtensionServiceImpl extends ServiceImpl<SystemExtensionMapper, SystemExtensionEntity>
        implements SystemExtensionService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionLifecycleOrchestrator lifecycleOrchestrator;
    private final ApplicationContext applicationContext;
    private final PluginDirectoryExtensionSyncService pluginDirectoryExtensionSyncService;

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
        if (!result) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_CREATE_FAILED);
        }
        return BaseResponse.success("安装成功，待应用");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<SystemExtensionResponseDTO> detail(Long id) {
        SystemExtensionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_NOT_FOUND);
        }
        SystemExtensionResponseDTO responseDTO = new SystemExtensionResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        fillAvatarFromDescriptors(responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(SystemExtensionUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR);
        }
        SystemExtensionEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_NOT_FOUND);
        }
        SystemExtensionEntity entity = new SystemExtensionEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<SystemExtensionResponseDTO> queryPage(BasePageRequest<SystemExtensionQueryRequestDTO> request) {
        IPage<SystemExtensionResponseDTO> page = PageUtils.buildPage(request);
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
    @Transactional(rollbackFor = Exception.class) // 建议开启事务
    public BaseResponse<String> uploadJar(MultipartFile file) {

        // 1. 基础校验
        if (file == null || file.isEmpty()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "请选择 jar 文件");
        }

        String originalName = file.getOriginalFilename();
        String safeJarName = ExtensionJarUtil.sanitizeJarFileName(originalName);

        // 校验后缀，防止 substring 报错
        if (!safeJarName.toLowerCase().endsWith(".jar")) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "仅支持 .jar 格式文件");
        }

        File pluginsDir = pluginManager.getPluginsDirectory();
        File destFile = new File(pluginsDir, safeJarName);
        if (destFile.exists()) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "plugins 目录下已存在同名文件: " + safeJarName);
        }

        String stem = safeJarName.substring(0, safeJarName.length() - 4);
        File tempJar = null;

        try {
            // 2. 暂存并解析文件
            try {
                tempJar = File.createTempFile("astro-ext-upload-", ".jar");
                file.transferTo(tempJar);
            } catch (IOException e) {
                log.error("暂存上传 jar 失败", e);
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPLOAD_FAILED, "保存临时文件失败");
            }

            Optional<SystemExtensionMetaData> jarMeta = ExtensionJarMetadataReader.tryLoad(tempJar);
            if (jarMeta.isEmpty()) {
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "未读取到扩展描述符，请确认 jar 内包含 AstroExtensionDescriptor SPI 配置");
            }

            // 3. 业务逻辑校验（Key 冲突检查）
            String key = ExtensionJarUtil.resolveExtensionKeyForUpload(jarMeta, stem);
            if (StringUtils.isBlank(key)) {
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "extensionKey 无效");
            }

            boolean exists = baseMapper.exists(new LambdaQueryWrapper<SystemExtensionEntity>()
                    .eq(SystemExtensionEntity::getExtensionKey, key));
            if (exists) {
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "扩展 Key 已存在: " + key);
            }

            // 4. 移动文件到目标目录
            try {
                if (!pluginsDir.exists()) {
                    Files.createDirectories(pluginsDir.toPath());
                }
                Files.move(tempJar.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                log.error("移动插件 jar 失败: {}", safeJarName, e);
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPLOAD_FAILED, "移动文件失败");
            }

            // 5. 构建并保存实体
            SystemExtensionEntity entity = buildExtensionEntity(key, safeJarName, stem, jarMeta.get());

            // 从最终文件应用 Manifest 默认值
            ExtensionJarUtil.applyManifestDefaults(destFile, entity);

            if (!save(entity)) {
                // 手动清理已移动的文件（如果没开事务的话）
                FileUtils.deleteQuietly(destFile);
                throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_CREATE_FAILED, "登记扩展记录失败");
            }

            // 6. 刷新插件容器
            refreshPluginSafely();

            return BaseResponse.success("上传成功并已登记。");

        } finally {
            // 确保临时文件一定被清理
            if (tempJar != null && tempJar.exists()) {
                FileUtils.deleteQuietly(tempJar);
            }
        }
    }

    /**
     * 提取实体构建逻辑，保持主流程简洁
     */
    private SystemExtensionEntity buildExtensionEntity(
            String key, String jarName, String stem, SystemExtensionMetaData meta) {

        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(key);
        entity.setJarName(jarName);

        // 上传接口仅接收文件，扩展元数据统一从 jar 内描述符读取。
        entity.setExtensionName(StringUtils.trimToNull(meta.extensionName()));
        entity.setType(StringUtils.trimToNull(meta.type()));
        entity.setVersion(StringUtils.trimToNull(meta.version()));
        entity.setAuthor(StringUtils.trimToNull(meta.author()));
        entity.setDescription(StringUtils.trimToNull(meta.description()));
        entity.setExtensionCode(StringUtils.trimToNull(meta.extensionCode()));
        entity.setAvatar(StringUtils.trimToNull(meta.avatar()));
        entity.setChangelog(StringUtils.trimToNull(meta.changelog()));
        entity.setMinServerVersion(StringUtils.trimToNull(meta.minServerVersion()));

        // 填充缺失默认值
        if (StringUtils.isBlank(entity.getExtensionName())) entity.setExtensionName(stem);
        if (StringUtils.isBlank(entity.getType()))
            entity.setType(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode());
        if (StringUtils.isBlank(entity.getVersion())) entity.setVersion("1.0.0");
        if (StringUtils.isBlank(entity.getAuthor())) entity.setAuthor("unknown");

        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        // 上传进入 plugins 目录并受平台托管的扩展，显式标记来源与发现方式。
        entity.setDiscoveryMechanism(SystemExtensionEnum.DiscoveryMechanismEnum.SPI.getCode());
        entity.setInstallSource(SystemExtensionEnum.InstallSourceEnum.PLUGIN_JAR_UPLOAD.getCode());

        return entity;
    }

    private void refreshPluginSafely() {
        try {
            pluginManager.reloadPlugins();
            pluginDirectoryExtensionSyncService.syncDiscoveredPlugins();
        } catch (Exception e) {
            log.error("上传后 reloadPlugins 失败，请手动重试: {}", e.getMessage());
        }
    }

}
