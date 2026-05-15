package com.astrsomn.server.service.vector;

import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecStore;
import com.astrsomn.api.vector.exception.AstVecStoreErrorEnum;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.service.AiVecSourceService;
import com.astrsomn.starter.runtime.langchain.vector.AstroVecSourceFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 将 {@link AiVecStoreEntity} 的增删映射到向量引擎侧集合（{@link VecStore#createCollection()} / {@link VecStore#dropCollection()}）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VectorStorePhysicalHandler {

    private static final String LOG_PREFIX = "[Astrsomn] [向量物理操作] ====> ";

    private final AstroVecSourceFactory vecSourceFactory;
    private final AiVecSourceService vecSourceService;

    /**
     * 创建物理 Collection
     */
    public void createPhysical(AiVecStoreEntity store) {
        validateParams(store);
        AiVecSourceEntity source = requireEnabledSource(store.getSourceId());
        validateDimension(store, source);

        try {
            // 确保向量源已注册/刷新
            vecSourceFactory.registerOrRefresh(source);

            VecSource vs = vecSourceFactory.tryGetActiveSource(source.getId())
                    .orElseThrow(() -> new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED, "向量源未就绪"));

            vs.openStore(store).createCollection();
            log.info("{} 物理集合创建成功 | Store: {} | Collection: {}", LOG_PREFIX, store.getId(), store.getCollectionName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("{} 创建物理集合失败 | Store: {} | 错误: {}", LOG_PREFIX, store.getId(), e.getMessage(), e);
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED, e.getMessage());
        }
    }

    /**
     * 删除物理 Collection
     */
    public void dropPhysical(AiVecStoreEntity snapshot) {
        if (Objects.isNull(snapshot) || Objects.isNull(snapshot.getSourceId()) || StringUtils.isBlank(snapshot.getCollectionName())) {
            return;
        }

        Optional<AiVecSourceEntity> sourceOpt = Optional.ofNullable(vecSourceService.getById(snapshot.getSourceId()));
        if (sourceOpt.isEmpty()) {
            log.warn("{} 物理删除跳过 | 原因: 数据源 {} 不存在 | 集合: {}", LOG_PREFIX, snapshot.getSourceId(), snapshot.getCollectionName());
            return;
        }

        AiVecSourceEntity source = sourceOpt.get();
        // 尝试获取活跃连接，否则创建一次性连接
        VecSource vs = vecSourceFactory.tryGetActiveSource(source.getId()).orElse(null);
        boolean isDisposable = Objects.isNull(vs);
        if (isDisposable) {
            vs = vecSourceFactory.bindSource(source);
        }

        try {
            vs.openStore(snapshot).dropCollection();
            log.info("{} 物理集合已删除 | Collection: {}", LOG_PREFIX, snapshot.getCollectionName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("{} 删除物理集合失败 | Store: {} | 错误: {}", LOG_PREFIX, snapshot.getId(), e.getMessage());
            throw new BusinessException(AstVecStoreErrorEnum.STORE_DELETE_FAILED, e.getMessage());
        } finally {
            if (isDisposable && Objects.nonNull(vs)) {
                shutdownQuietly(vs);
            }
        }
    }

    private AiVecSourceEntity requireEnabledSource(Long sourceId) {
        return Optional.ofNullable(vecSourceService.getById(sourceId))
                .filter(src -> "enabled".equalsIgnoreCase(StringUtils.trim(src.getStatus())))
                .orElseThrow(() -> new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "向量数据源不存在或未启用"));
    }

    private void validateParams(AiVecStoreEntity store) {
        if (Objects.isNull(store.getSourceId()) || StringUtils.isBlank(store.getCollectionName())) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "sourceId 或 collectionName 不能为空");
        }
    }

    private void validateDimension(AiVecStoreEntity store, AiVecSourceEntity src) {
        if (AiVecDriverEnum.Provider.QDRANT.getCode().equalsIgnoreCase(src.getExtensionCode())) {
            if (Objects.isNull(store.getDimension()) || store.getDimension() <= 0) {
                throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "Qdrant 向量库需要有效的 dimension");
            }
        }
    }

    private void shutdownQuietly(VecSource vs) {
        try {
            vs.shutdown();
        } catch (Exception e) {
            log.warn("{} 一次性句柄关闭异常: {}", LOG_PREFIX, e.getMessage());
        }
    }
}
