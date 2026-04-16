package org.astrsomn.server.service.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.langchain.extension.vector.VecSource;
import org.astrsomn.core.common.langchain.extension.vector.VecStore;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecStoreErrorEnum;
import org.astrsomn.server.service.AiVecSourceService;
import org.astrsomn.starter.langchain.vector.AstroVecSourceFactory;
import org.springframework.stereotype.Component;

/**
 * 将 {@link AiVecStoreEntity} 的增删映射到向量引擎侧集合（{@link VecStore#createCollection()} / {@link VecStore#dropCollection()}）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AstroVecStorePhysicalOps {

    private static final String STATUS_ENABLED = "ENABLED";

    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AiVecSourceService aiVecSourceService;

    public void createPhysical(AiVecStoreEntity store) {
        validateParams(store);
        AiVecSourceEntity src = requireEnabledSource(store.getSourceId());
        validateDimensionForQdrant(store, src);
        try {
            astroVecSourceFactory.registerOrRefresh(src);
            VecSource vs =
                    astroVecSourceFactory
                            .tryGetActiveSource(src.getId())
                            .orElseThrow(
                                    () ->
                                            new BusinessException(
                                                    AstVecStoreErrorEnum.STORE_CREATE_FAILED, "向量源未就绪"));
            VecStore vec = vs.openStore(store);
            vec.createCollection();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "[VecStore] createPhysical failed storeId={} sourceId={}",
                    store.getId(),
                    store.getSourceId(),
                    e);
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED, e.getMessage());
        }
    }

    public void dropPhysical(AiVecStoreEntity snapshot) {
        if (snapshot == null || snapshot.getSourceId() == null || StringUtils.isBlank(snapshot.getCollectionName())) {
            return;
        }
        AiVecSourceEntity src = aiVecSourceService.getById(snapshot.getSourceId());
        if (src == null) {
            log.warn(
                    "[VecStore] dropPhysical: source {} missing, skip vector drop for collection {}",
                    snapshot.getSourceId(),
                    snapshot.getCollectionName());
            return;
        }
        VecSource vs = astroVecSourceFactory.tryGetActiveSource(src.getId()).orElse(null);
        boolean disposable = false;
        if (vs == null) {
            vs = astroVecSourceFactory.bindSource(src);
            disposable = true;
        }
        try {
            vs.openStore(snapshot).dropCollection();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "[VecStore] dropPhysical failed storeId={} sourceId={}",
                    snapshot.getId(),
                    snapshot.getSourceId(),
                    e);
            throw new BusinessException(AstVecStoreErrorEnum.STORE_DELETE_FAILED, e.getMessage());
        } finally {
            if (disposable && vs != null) {
                try {
                    vs.shutdown();
                } catch (Exception e) {
                    log.warn("[VecStore] dropPhysical: one-off VecSource shutdown: {}", e.getMessage());
                }
            }
        }
    }

    private AiVecSourceEntity requireEnabledSource(Long sourceId) {
        AiVecSourceEntity src = aiVecSourceService.getById(sourceId);
        if (src == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "向量数据源不存在");
        }
        String st = StringUtils.defaultIfBlank(StringUtils.trimToNull(src.getStatus()), "");
        if (!STATUS_ENABLED.equalsIgnoreCase(st)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "向量数据源未启用");
        }
        return src;
    }

    private void validateParams(AiVecStoreEntity store) {
        if (store.getSourceId() == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "sourceId 不能为空");
        }
        if (StringUtils.isBlank(store.getCollectionName())) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "collectionName 不能为空");
        }
    }

    private void validateDimensionForQdrant(AiVecStoreEntity store, AiVecSourceEntity src) {
        String p = StringUtils.defaultIfBlank(StringUtils.trimToNull(src.getProvider()), "");
        if (AiVecDriverEnum.Provider.QDRANT.getCode().equalsIgnoreCase(p)) {
            if (store.getDimension() == null || store.getDimension() <= 0) {
                throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "Qdrant 向量库需要有效的 dimension");
            }
        }
    }
}
