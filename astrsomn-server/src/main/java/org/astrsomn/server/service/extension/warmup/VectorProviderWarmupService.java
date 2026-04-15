package org.astrsomn.server.service.extension.warmup;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Vector 扩展应用后增量预热：仅处理当前 env + enabled 向量源。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VectorProviderWarmupService {

    private final AiVecSourceMapper aiVecSourceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstroVecSourceFactory astroVecSourceFactory;

    public void warmupEnabledSourcesForProvider(String providerCode) {
        String provider = StringUtils.trimToNull(providerCode);
        if (provider == null) {
            return;
        }
        LambdaQueryWrapper<AiVecSourceEntity> w = new LambdaQueryWrapper<>();
        w.eq(AiVecSourceEntity::getProvider, provider)
                .eq(AiVecSourceEntity::getDeleted, Boolean.FALSE)
                .and(q -> q.eq(AiVecSourceEntity::getStatus, "ENABLED").or().eq(AiVecSourceEntity::getStatus, "enabled"));
        String env = queryEnvParamHelper.effectiveEnvCode();
        if (env != null) {
            w.eq(AiVecSourceEntity::getEnvCode, env);
        }
        List<AiVecSourceEntity> enabledSources = aiVecSourceMapper.selectList(w);
        for (AiVecSourceEntity source : enabledSources) {
            try {
                astroVecSourceFactory.registerOrRefresh(source);
            } catch (Exception e) {
                log.warn(
                        "Vector provider warmup failed: provider={} sourceId={} message={}",
                        provider,
                        source.getId(),
                        e.getMessage());
            }
        }
    }
}

