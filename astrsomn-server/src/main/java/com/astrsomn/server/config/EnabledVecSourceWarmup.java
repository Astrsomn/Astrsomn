package com.astrsomn.server.config;

import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.constant.AiVecSourceEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;

import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.astrsomn.starter.runtime.vector.mapper.AstAiVecSourceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Component
public class EnabledVecSourceWarmup implements ApplicationListener<ApplicationReadyEvent> {

    private static final String LOG_PREFIX = "[Astrsomn] [向量库连接池预热器] ====> ";
    private static final String STATUS_DISABLED = AiVecDriverEnum.StatusEnum.DISABLED.getCode();
    private static final String STATUS_ENABLED = AiVecSourceEnum.StatusEnum.ENABLED.getCode();

    private AstAiVecSourceMapper vecSourceMapper;
    private AstroVecSourceFactory vecSourceFactory;

    @Autowired(required = false)
    public void setVecSourceMapper(AstAiVecSourceMapper vecSourceMapper) {
        this.vecSourceMapper = vecSourceMapper;
    }

    @Autowired(required = false)
    public void setVecSourceFactory(AstroVecSourceFactory vecSourceFactory) {
        this.vecSourceFactory = vecSourceFactory;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (vecSourceMapper == null || vecSourceFactory == null) {
            log.warn("{} 必要依赖未注入，跳过向量源预热", LOG_PREFIX);
            return;
        }
        warmup();
    }

    private void warmup() {
        try {
            doWarmup();
        } catch (Exception e) {
            log.warn("{} 向量源预热失败，将在 2s 后重试 | 异常: {}", LOG_PREFIX, e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("{} 向量源预热重试被中断", LOG_PREFIX);
                return;
            }
            try {
                doWarmup();
            } catch (Exception e2) {
                log.error("{} 向量源预热重试失败 | 异常: {}", LOG_PREFIX, e2.getMessage());
            }
        }
    }

    private void doWarmup() {
        List<AiVecSourceEntity> sources = fetchEnabledSources();

        if (sources.isEmpty()) {
            log.debug("{} 没有需要预热的启用向量源", LOG_PREFIX);
            return;
        }

        log.info("{} 开始执行预热任务 | 待处理数量: {}", LOG_PREFIX, sources.size());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger skipCount = new AtomicInteger(0);
        AtomicInteger disableCount = new AtomicInteger(0);

        sources.forEach(source -> {
            if (!registerSource(source)) {
                skipCount.incrementAndGet();
                return;
            }

            vecSourceFactory.tryGetActiveSource(source.getId())
                    .ifPresentOrElse(
                            vecSource -> {
                                if (checkConnection(vecSource, source)) {
                                    successCount.incrementAndGet();
                                } else {
                                    disableCount.incrementAndGet();
                                }
                            },
                            () -> {
                                log.warn("{} 预热跳过 | 原因: 注册后无法从缓存获取实例 | ID: {} | 名称: {}",
                                        LOG_PREFIX, source.getId(), source.getName());
                                skipCount.incrementAndGet();
                            }
                    );
        });

        log.info("{} 向量源预热完成 | 总数: {} | 成功: {} | 禁用: {} | 跳过: {}",
                LOG_PREFIX, sources.size(), successCount.get(), disableCount.get(), skipCount.get());
    }

    private List<AiVecSourceEntity> fetchEnabledSources() {
        return vecSourceMapper.selectList(new LambdaQueryWrapper<AiVecSourceEntity>()
                .eq(AiVecSourceEntity::getDeleted, Boolean.FALSE)
                .eq(AiVecSourceEntity::getStatus, STATUS_ENABLED));
    }

    private boolean registerSource(AiVecSourceEntity source) {
        try {
            vecSourceFactory.registerOrRefresh(source);
            return true;
        } catch (Throwable ex) {
            log.warn("{} 注册失败 | ID: {} | 名称: {} | 异常: {}",
                    LOG_PREFIX, source.getId(), source.getName(), ex.getMessage());
            return false;
        }
    }

    private boolean checkConnection(VecSource vecSource, AiVecSourceEntity source) {
        try {
            if (vecSource.testConnection()) {
                log.debug("{} 连接成功 | ID: {} | 名称: {}", LOG_PREFIX, source.getId(), source.getName());
                return true;
            }
            handleFailure(source, "连接测试返回 false (不可达)");
        } catch (Throwable ex) {
            handleFailure(source, "连接测试异常: " + ex.getMessage(), ex);
        }
        return false;
    }

    private void handleFailure(AiVecSourceEntity source, String reason) {
        handleFailure(source, reason, null);
    }

    private void handleFailure(AiVecSourceEntity source, String reason, Throwable ex) {
        Long id = source.getId();
        if (Objects.isNull(id)) {
            return;
        }

        vecSourceFactory.removeActiveSource(id);

        int rows = vecSourceMapper.update(null, new LambdaUpdateWrapper<AiVecSourceEntity>()
                .eq(AiVecSourceEntity::getId, id)
                .set(AiVecSourceEntity::getStatus, STATUS_DISABLED));

        if (ex != null) {
            log.warn("{} 预热失败已禁用 (rows={}) | ID: {} | 名称: {} | 原因: {}",
                    LOG_PREFIX, rows, id, source.getName(), reason, ex);
        } else {
            log.warn("{} 预热失败已禁用 (rows={}) | ID: {} | 名称: {} | 原因: {}",
                    LOG_PREFIX, rows, id, source.getName(), reason);
        }
    }
}
