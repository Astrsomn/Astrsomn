package org.astrsomn.server.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 进程启动后预热「已启用」向量源的运行时连接，与实例配置的 {@code envCode} 一致。
 */
@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class EnabledVecSourceWarmup implements ApplicationRunner {

    private static final String STATUS_ENABLED = "ENABLED";

    private final AiVecSourceMapper aiVecSourceMapper;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public void run(ApplicationArguments args) {
        LambdaQueryWrapper<AiVecSourceEntity> w = new LambdaQueryWrapper<>();
        w.eq(AiVecSourceEntity::getStatus, STATUS_ENABLED);
        w.eq(AiVecSourceEntity::getDeleted, Boolean.FALSE);
        String env = queryEnvParamHelper.effectiveEnvCode();
        if (env != null) {
            w.eq(AiVecSourceEntity::getEnvCode, env);
        }
        List<AiVecSourceEntity> list = aiVecSourceMapper.selectList(w);
        if (list.isEmpty()) {
            log.debug("No ENABLED vec sources to warm up for env={}", env);
            return;
        }
        for (AiVecSourceEntity e : list) {
            try {
                astroVecSourceFactory.registerOrRefresh(e);
            } catch (Exception ex) {
                log.warn("Warmup vec source failed id={} name={}: {}", e.getId(), e.getName(), ex.getMessage());
            }
        }
        log.info("Vec source warmup finished: {} source(s) for env={}", list.size(), env);
    }
}
