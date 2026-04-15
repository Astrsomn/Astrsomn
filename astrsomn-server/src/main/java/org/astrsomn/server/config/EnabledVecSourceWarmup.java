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
        String env = queryEnvParamHelper.effectiveEnvCode();
        List<AiVecSourceEntity> enabledVecSources = getEnabledVecSources(env);
        
        if (enabledVecSources.isEmpty()) {
            log.debug("当前环境 {} 没有需要预热的启用向量源", env);
            return;
        }
        
        int ok = 0;
        int skipped = 0;
        int disabled = 0;
        
        for (AiVecSourceEntity source : enabledVecSources) {
            Long sourceId = source.getId();
            
            if (!registerVecSource(source, sourceId)) {
                skipped++;
                continue;
            }
            
            VecSource vecSource = getVecSourceFromCache(sourceId);
            if (vecSource == null) {
                log.warn("预热：注册后缓存中无向量源实例，跳过测试，ID={}，名称={}，提供商={}，状态={}",
                        sourceId, source.getName(), source.getProvider(), source.getStatus());
                skipped++;
                continue;
            }
            
            if (!testVecSourceConnection(source, sourceId, vecSource)) {
                disabled++;
            } else {
                ok++;
            }
        }
        
        log.info("向量源预热完成：环境={}，成功={}，跳过={}，禁用={}，总数={}",
                env, ok, skipped, disabled, enabledVecSources.size());
    }

    /**
     * 获取已启用的向量源列表
     */
    private List<AiVecSourceEntity> getEnabledVecSources(String env) {
        LambdaQueryWrapper<AiVecSourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiVecSourceEntity::getDeleted, Boolean.FALSE);
        queryWrapper.and(q -> q.eq(AiVecSourceEntity::getStatus, "ENABLED").or().eq(AiVecSourceEntity::getStatus, "enabled"));
        
        if (env != null) {
            queryWrapper.eq(AiVecSourceEntity::getEnvCode, env);
        }
        
        return aiVecSourceMapper.selectList(queryWrapper);
    }

    /**
     * 注册向量源
     */
    private boolean registerVecSource(AiVecSourceEntity source, Long sourceId) {
        try {
            astroVecSourceFactory.registerOrRefresh(source);
            return true;
        } catch (Throwable ex) {
            log.warn("预热注册失败，跳过，ID={}，名称={}，提供商={}：{}",
                    sourceId, source.getName(), source.getProvider(), ex.getMessage());
            return false;
        }
    }

    /**
     * 从缓存获取向量源实例
     */
    private VecSource getVecSourceFromCache(Long sourceId) {
        return astroVecSourceFactory.tryGetActiveSource(sourceId).orElse(null);
    }

    /**
     * 测试向量源连接
     */
    private boolean testVecSourceConnection(AiVecSourceEntity source, Long sourceId, VecSource vecSource) {
        try {
            if (!vecSource.testConnection()) {
                markDisabledAfterFailedTest(sourceId, source, "连接测试返回 false");
                return false;
            }
            return true;
        } catch (Throwable ex) {
            markDisabledAfterFailedTest(sourceId, source, "连接测试异常：" + ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * 测试失败后标记为禁用
     */
    private void markDisabledAfterFailedTest(Long sourceId, AiVecSourceEntity source, String detail) {
        markDisabledAfterFailedTest(sourceId, source, detail, null);
    }

    /**
     * 测试失败后标记为禁用（带异常）
     */
    private void markDisabledAfterFailedTest(Long sourceId, AiVecSourceEntity source, String detail, Throwable ex) {
        if (sourceId == null) {
            return;
        }
        
        // 从缓存中移除
        astroVecSourceFactory.removeActiveSource(sourceId);
        
        // 更新数据库状态
        LambdaUpdateWrapper<AiVecSourceEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiVecSourceEntity::getId, sourceId).set(AiVecSourceEntity::getStatus, STATUS_DISABLED);
        
        try {
            int rows = aiVecSourceMapper.update(null, updateWrapper);
            if (ex != null) {
                log.warn("预热测试失败，状态已设置为禁用（影响行数={}），ID={}，名称={}，提供商={}：{}",
                        rows, source != null ? source.getId() : null, source != null ? source.getName() : null,
                        source != null ? source.getProvider() : null, detail, ex);
            } else {
                log.warn("预热测试失败，状态已设置为禁用（影响行数={}），ID={}，名称={}，提供商={}：{}",
                        rows, source != null ? source.getId() : null, source != null ? source.getName() : null,
                        source != null ? source.getProvider() : null, detail);
            }
        } catch (Exception updateEx) {
            log.error("预热失败：无法在数据库中设置禁用状态，ID={}：{}", sourceId, updateEx.getMessage(), updateEx);
        }
    }
}
