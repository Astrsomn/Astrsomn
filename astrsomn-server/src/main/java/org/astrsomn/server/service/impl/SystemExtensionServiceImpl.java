package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.dto.extension.*;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.AstroExtensionDescriptor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.SystemExtensionErrorEnum;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.astrsomn.server.plugin.metadata.ExtensionJarMetadataReader;
import org.astrsomn.server.plugin.registry.SystemExtensionRegistry;
import org.astrsomn.server.service.SystemExtensionService;
import org.astrsomn.server.service.extension.lifecycle.SystemExtensionLifecycleOrchestrator;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.plugin.AstrsomnPluginManager;
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
import org.astrsomn.server.util.ExtensionJarUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemExtensionServiceImpl extends ServiceImpl<SystemExtensionMapper, SystemExtensionEntity>
        implements SystemExtensionService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstrsomnPluginManager pluginManager;
    private final SystemExtensionLifecycleOrchestrator lifecycleOrchestrator;
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
    public BaseResponse<String> uploadJar(
            MultipartFile file,
            String extensionKey,
            String extensionName,
            String type,
            String version,
            String author,
            String description,
            String providerCode) {

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

            // 3. 业务逻辑校验（Key 冲突检查）
            String key = ExtensionJarUtil.resolveExtensionKeyForUpload(StringUtils.trimToNull(extensionKey), jarMeta, stem);
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
            SystemExtensionEntity entity = buildExtensionEntity(key, safeJarName, stem, jarMeta,
                    extensionName, type, version, author, description, providerCode);

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
            String key, String jarName, String stem, Optional<SystemExtensionMetaData> meta,
            String name, String type, String ver, String author, String desc, String provider) {

        SystemExtensionEntity entity = new SystemExtensionEntity();
        entity.setExtensionKey(key);
        entity.setJarName(jarName);

        // 合并元数据：优先使用传入参数，其次使用 Jar 包内的，最后使用默认值
        entity.setExtensionName(ExtensionJarUtil.pickMeta(name, meta.map(SystemExtensionMetaData::extensionName)));
        entity.setType(ExtensionJarUtil.pickMeta(type, meta.map(SystemExtensionMetaData::type)));
        entity.setVersion(ExtensionJarUtil.pickMeta(ver, meta.map(SystemExtensionMetaData::version)));
        entity.setAuthor(ExtensionJarUtil.pickMeta(author, meta.map(SystemExtensionMetaData::author)));
        entity.setDescription(ExtensionJarUtil.pickMeta(desc, meta.map(SystemExtensionMetaData::description)));
        entity.setProviderCode(ExtensionJarUtil.pickMeta(provider, meta.map(SystemExtensionMetaData::providerCode)));
        entity.setAvatar(ExtensionJarUtil.pickMeta(null, meta.map(SystemExtensionMetaData::avatar)));

        // 填充缺失默认值
        if (StringUtils.isBlank(entity.getExtensionName())) entity.setExtensionName(stem);
        if (StringUtils.isBlank(entity.getType())) entity.setType(SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode());
        if (StringUtils.isBlank(entity.getVersion())) entity.setVersion("1.0.0");
        if (StringUtils.isBlank(entity.getAuthor())) entity.setAuthor("unknown");

        entity.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        entity.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());

        return entity;
    }

    private void refreshPluginSafely() {
        try {
            pluginManager.reloadPlugins();
        } catch (Exception e) {
            log.error("上传后 reloadPlugins 失败，请手动重试: {}", e.getMessage());
        }
    }

}
