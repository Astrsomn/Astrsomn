package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.entity.AiVecDriverEntity;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.langchain.extension.vector.VecDriver;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.SystemExtensionErrorEnum;
import org.astrsomn.core.mapper.AiVecDriverMapper;
import org.astrsomn.server.service.SystemExtensionVecDriverSyncService;
import org.astrsomn.starter.plugin.AstrsomnPluginManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ServiceLoader;

@Service
@RequiredArgsConstructor
public class SystemExtensionVecDriverSyncServiceImpl implements SystemExtensionVecDriverSyncService {

    private final AiVecDriverMapper aiVecDriverMapper;
    private final AstrsomnPluginManager pluginManager;

    @Override
    public void upsertFromExtension(SystemExtensionEntity extension) {
        String extensionKey = StringUtils.trimToNull(extension.getExtensionKey());
        if (extensionKey == null) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_PARAM_ERROR, "extensionKey 为空，无法同步向量驱动");
        }
        VecDriver driver = resolveVecDriver(extension);
        if (driver == null) {
            throw new BusinessException(
                    SystemExtensionErrorEnum.EXTENSION_APPLY_FAILED,
                    "未找到 extensionKey=" + extensionKey + " 的 VecDriver 实现（请确认已加载插件或已引入对应模块）");
        }
        AiVecDriverEntity incoming = driver.getDriverEntity();
        if (incoming == null || StringUtils.isBlank(incoming.getProvider())) {
            throw new BusinessException(SystemExtensionErrorEnum.EXTENSION_APPLY_FAILED, "VecDriver#getDriverEntity 数据无效");
        }

        AiVecDriverEntity existing =
                aiVecDriverMapper.selectOne(
                        new LambdaQueryWrapper<AiVecDriverEntity>()
                                .eq(AiVecDriverEntity::getProvider, incoming.getProvider())
                                .last("LIMIT 1"));
        if (existing != null) {
            existing.setDriverName(incoming.getDriverName());
            existing.setParams(incoming.getParams());
            aiVecDriverMapper.updateById(existing);
        } else {
            incoming.setId(null);
            aiVecDriverMapper.insert(incoming);
        }
    }

    @Override
    public void removeDriverRowForProvider(String providerCode) {
        String p = StringUtils.trimToNull(providerCode);
        if (p == null) {
            return;
        }
        aiVecDriverMapper.delete(new LambdaQueryWrapper<AiVecDriverEntity>().eq(AiVecDriverEntity::getProvider, p));
    }

    private VecDriver resolveVecDriver(SystemExtensionEntity extension) {
        String key = extension.getExtensionKey().trim();
        String jarName = StringUtils.trimToNull(extension.getJarName());
        if (jarName != null) {
            List<VecDriver> fromJar = pluginManager.getVecDriversForJar(jarName);
            for (VecDriver v : fromJar) {
                if (key.equals(v.getExtensionKey())) {
                    return v;
                }
            }
        }
        ServiceLoader<VecDriver> loader = ServiceLoader.load(VecDriver.class, Thread.currentThread().getContextClassLoader());
        for (VecDriver v : loader) {
            if (key.equals(v.getExtensionKey())) {
                return v;
            }
        }
        return null;
    }
}
