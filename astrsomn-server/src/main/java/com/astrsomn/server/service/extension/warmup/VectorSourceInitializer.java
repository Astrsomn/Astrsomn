package com.astrsomn.server.service.extension.warmup;

import com.astrsomn.api.runtime.common.constant.AiVecSourceEnum;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.runtime.langchain.vector.AstroVecSourceFactory;
import com.astrsomn.starter.runtime.mapper.AstAiVecSourceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * TODO [向量源初始化器] Vector 扩展应用后增量预热：仅处理当前 env + enabled 向量源。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VectorSourceInitializer {

    private static final String LOG_PREFIX = "[Astrsomn] [向量源定向预热] ====> ";
    private static final String STATUS_ENABLED = AiVecSourceEnum.StatusEnum.ENABLED.getCode();

    private final AstAiVecSourceMapper vecSourceMapper;
    private final QueryEnvParamHelper envParamHelper;
    private final AstroVecSourceFactory vecSourceFactory;

    /**
     * 为指定的厂商定向预热已启用的向量源
     */
    public void warmupEnabledSourcesByProvider(String extensionCode) {
        Optional.ofNullable(StringUtils.trimToNull(extensionCode))
                .ifPresent(provider -> {
                    String env = envParamHelper.effectiveEnvCode();
                    List<AiVecSourceEntity> sources = fetchSourcesByProvider(provider, env);

                    if (sources.isEmpty()) {
                        log.debug("{} 厂商 [{}] 无需预热的启用数据源 | 环境: {}", LOG_PREFIX, provider, env);
                        return;
                    }

                    sources.forEach(this::safeRegister);
                    log.info("{} 厂商定向预热完成 | 厂商: {} | 数量: {}", LOG_PREFIX, provider, sources.size());
                });
    }

    /**
     * 根据厂商和环境查询符合条件的向量源
     */
    private List<AiVecSourceEntity> fetchSourcesByProvider(String provider, String env) {
        return vecSourceMapper.selectList(new LambdaQueryWrapper<AiVecSourceEntity>()
                .eq(AiVecSourceEntity::getExtensionCode, provider)
                .eq(AiVecSourceEntity::getDeleted, false)
                .eq(AiVecSourceEntity::getStatus, STATUS_ENABLED)
                .eq(Objects.nonNull(env), AiVecSourceEntity::getEnvCode, env));
    }

    /**
     * 安全注册向量源，隔离异常防止中断
     */
    private void safeRegister(AiVecSourceEntity source) {
        try {
            vecSourceFactory.registerOrRefresh(source);
        } catch (Exception e) {
            log.warn("{} 预热失败 | 厂商: {} | ID: {} | 异常: {}",
                    LOG_PREFIX, source.getExtensionCode(), source.getId(), e.getMessage());
        }
    }
}

