package com.astrsomn.server.service.system.extension.warmup;

import com.astrsomn.api.vector.constant.AiVecSourceEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.common.utils.StringUtils;

import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.astrsomn.starter.runtime.vector.mapper.AstAiVecSourceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class VectorSourceInitializer {

    private static final String LOG_PREFIX = "[Astrsomn] [向量源定向预热] ====> ";
    private static final String STATUS_ENABLED = AiVecSourceEnum.StatusEnum.ENABLED.getCode();

    private final AstAiVecSourceMapper vecSourceMapper;

    private final AstroVecSourceFactory vecSourceFactory;

    
    public void warmupEnabledSourcesByProvider(String extensionCode) {
        Optional.ofNullable(StringUtils.trimToNull(extensionCode))
                .ifPresent(provider -> {

                    List<AiVecSourceEntity> sources = fetchSourcesByProvider(provider);

                    if (sources.isEmpty()) {
                        log.debug("{} 厂商 [{}] 无需预热的启用数据源 | 环境: {}", LOG_PREFIX, provider);
                        return;
                    }

                    sources.forEach(this::safeRegister);
                    log.info("{} 厂商定向预热完成 | 厂商: {} | 数量: {}", LOG_PREFIX, provider, sources.size());
                });
    }

    
    private List<AiVecSourceEntity> fetchSourcesByProvider(String provider) {
        return vecSourceMapper.selectList(new LambdaQueryWrapper<AiVecSourceEntity>()
                .eq(AiVecSourceEntity::getExtensionCode, provider)
                .eq(AiVecSourceEntity::getDeleted, false)
                .eq(AiVecSourceEntity::getStatus, STATUS_ENABLED)
                );
    }

    
    private void safeRegister(AiVecSourceEntity source) {
        try {
            vecSourceFactory.registerOrRefresh(source);
        } catch (Exception e) {
            log.warn("{} 预热失败 | 厂商: {} | ID: {} | 异常: {}",
                    LOG_PREFIX, source.getExtensionCode(), source.getId(), e.getMessage());
        }
    }
}

