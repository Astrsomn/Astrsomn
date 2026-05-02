package com.astrsomn.server.service.extension.lifecycle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.api.runtime.common.constant.SystemExtensionEnum;
import com.astrsomn.api.runtime.common.entity.SystemExtensionEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.api.runtime.exception.SystemExtensionErrorEnum;
import com.astrsomn.starter.runtime.mapper.SystemExtensionMapper;
import com.astrsomn.server.service.extension.support.SystemExtensionSourceHelper;
import com.astrsomn.server.service.extension.capability.ExtensionCapabilityResolver;
import com.astrsomn.server.service.extension.dependency.ExtensionDependencyGuard;
import com.astrsomn.starter.runtime.plugin.AstrsomnPluginManager;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemExtensionLifecycleOrchestrator {

    private final SystemExtensionMapper systemExtensionMapper;
    private final ExtensionStrategyResolver strategyResolver;
    private final ExtensionDependencyGuard dependencyGuard;
    private final ExtensionCapabilityResolver capabilityResolver;
    private final AstrsomnPluginManager pluginManager;

    public BaseResponse<String> apply(Long id) {
        SystemExtensionEntity extension = mustGet(id);
        ExtensionDependencyGuard.DependencyCheckResult dep = dependencyGuard.assertDependencies(extension);
        ExtensionLifecycleStrategy strategy = strategyResolver.resolve(extension.getType());
        strategy.apply(extension);
        assertCapabilityReady(extension);

        extension.setApplied(SystemExtensionEnum.ApplyStatusEnum.Y.getCode());
        extension.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.APPLIED.getCode());
        updateRow(extension);

        return BaseResponse.success(buildSuccessMessage("插件应用成功", dep.warnings()));
    }

    public BaseResponse<String> revoke(Long id) {
        SystemExtensionEntity extension = mustGet(id);
        if (!SystemExtensionEnum.ExtensionInstallStatusEnum.APPLIED.getCode().equals(extension.getStatus())) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "当前不是已应用状态，无需取消应用");
        }
        ExtensionLifecycleStrategy strategy = strategyResolver.resolve(extension.getType());
        strategy.revoke(extension);

        extension.setApplied(SystemExtensionEnum.ApplyStatusEnum.N.getCode());
        extension.setStatus(SystemExtensionEnum.ExtensionInstallStatusEnum.INSTALLED.getCode());
        updateRow(extension);

        return BaseResponse.success("已恢复为已安装");
    }

    public BaseResponse<String> uninstall(Long id) {
        SystemExtensionEntity extension = mustGet(id);
        if (!SystemExtensionSourceHelper.canUninstall(extension)) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PERMISSION_DENIED, "Classpath 依赖扩展不支持卸载，仅支持启用/禁用");
        }
        ExtensionLifecycleStrategy strategy = strategyResolver.resolve(extension.getType());
        strategy.uninstall(extension);

        int deleted = systemExtensionMapper.deleteById(id);
        if (deleted <= 0) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UNINSTALL_FAILED);
        }
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null && SystemExtensionSourceHelper.canDeleteJarFromDisk(extension)) {
            tryDeletePluginJarFromDisk(jarName);
        }
        return BaseResponse.success("卸载成功");
    }

    private void assertCapabilityReady(SystemExtensionEntity extension) {
        String type = StringUtils.trimToNull(extension.getType());
        if (SystemExtensionEnum.ExtensionTypeEnum.MODEL_PROVIDER.getCode().equals(type)
                && !capabilityResolver.hasModelProviderCapability(extension)) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_APPLY_FAILED, "未发现可用的 ModelProviderHandler（SPI）");
        }
        if (SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE.getCode().equals(type)
                && !capabilityResolver.hasVectorDriverCapability(extension)) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_APPLY_FAILED, "未发现可用的 VecDriver（SPI）");
        }
    }

    private SystemExtensionEntity mustGet(Long id) {
        SystemExtensionEntity entity = systemExtensionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_NOT_FOUND);
        }
        return entity;
    }

    private void updateRow(SystemExtensionEntity extension) {
        int updated = systemExtensionMapper.updateById(extension);
        if (updated <= 0) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_UPDATE_FAILED);
        }
    }

    private static String buildSuccessMessage(String base, List<String> warnings) {
        List<String> safeWarnings = warnings == null ? List.of() : warnings;
        if (safeWarnings.isEmpty()) {
            return base;
        }
        List<String> trimmed = new ArrayList<>();
        for (String warning : safeWarnings) {
            String w = StringUtils.trimToNull(warning);
            if (w != null) {
                trimmed.add(w);
            }
        }
        if (trimmed.isEmpty()) {
            return base;
        }
        return base + "（存在软依赖告警: " + String.join(" | ", trimmed) + "）";
    }

    private void tryDeletePluginJarFromDisk(String jarName) {
        final String safeName;
        try {
            safeName = sanitizeJarFileName(jarName);
        } catch (IllegalArgumentException e) {
            log.warn("卸载时跳过删除 jar，文件名不合法: {}", jarName);
            return;
        }
        File pluginsDir = pluginManager.getPluginsDirectory();
        File jarFile = new File(pluginsDir, safeName);
        try {
            String dirCanon = pluginsDir.getCanonicalPath();
            String fileCanon = jarFile.getCanonicalPath();
            if (!fileCanon.startsWith(dirCanon + File.separator)) {
                log.warn("卸载时跳过删除 jar，路径不在 plugins 目录内: {}", fileCanon);
                return;
            }
        } catch (IOException e) {
            log.warn("解析插件 jar 路径失败: {}", jarName, e);
            return;
        }
        if (!jarFile.isFile()) {
            log.debug("卸载时 plugins 下无此文件，跳过删除: {}", jarFile.getAbsolutePath());
            return;
        }
        try {
            Files.deleteIfExists(jarFile.toPath());
            log.info("已删除卸载插件 jar: {}", jarFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn("删除插件 jar 失败（可手动删除）: {}", jarFile.getAbsolutePath(), e);
        }
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
}

