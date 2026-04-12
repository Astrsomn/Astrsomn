package org.astrsomn.server.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.langchain.extension.vector.VecSource;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 进程启动后预热「已启用」向量源的运行时连接（按 sourceId 缓存在 {@link AstroVecSourceFactory}），与实例配置的 {@code envCode} 一致。
 * <p>
 * 无待预热记录时不执行后续逻辑；注册后若缓存中无句柄则仅告警并跳过测试，不把数据源标为停用。
 * 仅当 {@link VecSource#testConnection()} 返回 false 或抛错时，将状态更新为 {@link AiVecDriverEnum.StatusEnum#DISABLED}。
 */
@Slf4j
@Component
@Order(20)
@RequiredArgsConstructor
public class EnabledVecSourceWarmup implements ApplicationRunner {

    private static final String STATUS_DISABLED = AiVecDriverEnum.StatusEnum.DISABLED.getCode();

    private final AiVecSourceMapper aiVecSourceMapper;
    private final AstroVecSourceFactory astroVecSourceFactory;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public void run(ApplicationArguments args) {
        LambdaQueryWrapper<AiVecSourceEntity> w = new LambdaQueryWrapper<>();
        w.eq(AiVecSourceEntity::getDeleted, Boolean.FALSE);
        w.and(
                q ->
                        q.eq(AiVecSourceEntity::getStatus, "ENABLED")
                                .or()
                                .eq(AiVecSourceEntity::getStatus, "enabled"));
        String env = queryEnvParamHelper.effectiveEnvCode();
        if (env != null) {
            w.eq(AiVecSourceEntity::getEnvCode, env);
        }
        List<AiVecSourceEntity> list = aiVecSourceMapper.selectList(w);
        if (list.isEmpty()) {
            log.debug("No enabled vec sources to warm up for env={}", env);
            return;
        }
        int ok = 0;
        int skipped = 0;
        int disabled = 0;
        for (AiVecSourceEntity e : list) {
            Long id = e.getId();
            try {
                astroVecSourceFactory.registerOrRefresh(e);
            } catch (Throwable ex) {
                log.warn(
                        "Warmup registerOrRefresh failed, skip id={} name={} provider={}: {}",
                        id,
                        e.getName(),
                        e.getProvider(),
                        ex.getMessage());
                skipped++;
                continue;
            }
            VecSource vs = astroVecSourceFactory.tryGetActiveSource(id).orElse(null);
            if (vs == null) {
                log.warn(
                        "Warmup: no VecSource in cache after registerOrRefresh, skip test id={} name={} provider={} status={}",
                        id,
                        e.getName(),
                        e.getProvider(),
                        e.getStatus());
                skipped++;
                continue;
            }
            try {
                if (!vs.testConnection()) {
                    markDisabledAfterFailedTest(id, e, "testConnection returned false", null);
                    disabled++;
                } else {
                    ok++;
                }
            } catch (Throwable ex) {
                markDisabledAfterFailedTest(id, e, ex.getMessage(), ex);
                disabled++;
            }
        }
        log.info(
                "Vec source warmup finished: env={} ok={} skipped={} disabled={} total={}",
                env,
                ok,
                skipped,
                disabled,
                list.size());
    }

    private void markDisabledAfterFailedTest(Long sourceId, AiVecSourceEntity e, String detail, Throwable ex) {
        if (sourceId == null) {
            return;
        }
        astroVecSourceFactory.removeActiveSource(sourceId);
        LambdaUpdateWrapper<AiVecSourceEntity> uw = new LambdaUpdateWrapper<>();
        uw.eq(AiVecSourceEntity::getId, sourceId).set(AiVecSourceEntity::getStatus, STATUS_DISABLED);
        try {
            int n = aiVecSourceMapper.update(null, uw);
            if (ex != null) {
                log.warn(
                        "Warmup test failed, status set to disabled (rows={}) id={} name={} provider={}: {}",
                        n,
                        e != null ? e.getId() : null,
                        e != null ? e.getName() : null,
                        e != null ? e.getProvider() : null,
                        detail,
                        ex);
            } else {
                log.warn(
                        "Warmup test failed, status set to disabled (rows={}) id={} name={} provider={}: {}",
                        n,
                        e != null ? e.getId() : null,
                        e != null ? e.getName() : null,
                        e != null ? e.getProvider() : null,
                        detail);
            }
        } catch (Exception updateEx) {
            log.error(
                    "Warmup failed to set disabled in DB id={}: {}",
                    sourceId,
                    updateEx.getMessage(),
                    updateEx);
        }
    }
}
