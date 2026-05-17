package com.astrsomn.server.service.impl;

import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.exception.AstVecStoreErrorEnum;
import com.astrsomn.api.vector.dto.vecstore.*;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecStoreMapper;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecStore;
import com.astrsomn.server.service.AiInstanceService;
import com.astrsomn.server.service.AiVecSourceService;
import com.astrsomn.server.service.AiVecStoreService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.server.service.vector.VectorStorePhysicalHandler;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecStoreServiceImpl extends ServiceImpl<AiVecStoreMapper, AiVecStoreEntity> implements AiVecStoreService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final VectorStorePhysicalHandler astroVecStorePhysicalOps;
    private final AiVecSourceService aiVecSourceService;
    private final AiInstanceService aiInstanceService;
    private final AstroVecSourceFactory astroVecSourceFactory;

    private static void mergeVecStoreUpdate(AiVecStoreEntity target, AiVecStoreUpdateRequestDTO req) {
        if (req.getSourceId() != null) {
            target.setSourceId(req.getSourceId());
        }
        if (req.getCollectionName() != null) {
            target.setCollectionName(req.getCollectionName());
        }
        if (req.getDimension() != null) {
            target.setDimension(req.getDimension());
        }
        if (req.getDistanceMetric() != null) {
            target.setDistanceMetric(req.getDistanceMetric());
        }
        if (req.getMetadataSchema() != null) {
            target.setMetadataSchema(req.getMetadataSchema());
        }
        if (req.getInstanceKey() != null) {
            target.setInstanceKey(req.getInstanceKey());
        }
        if (req.getModelKey() != null) {
            target.setModelKey(req.getModelKey());
        }
        if (req.getChunkStrategy() != null) {
            target.setChunkStrategy(req.getChunkStrategy());
        }
        if (req.getChunkSize() != null) {
            target.setChunkSize(req.getChunkSize());
        }
        if (req.getChunkOverlap() != null) {
            target.setChunkOverlap(req.getChunkOverlap());
        }
        if (req.getDenseWeight() != null) {
            target.setDenseWeight(req.getDenseWeight());
        }
        if (req.getInstructionPrefix() != null) {
            target.setInstructionPrefix(req.getInstructionPrefix());
        }

    }

    private static boolean physicalChanged(AiVecStoreEntity before, AiVecStoreEntity after, AiVecSourceEntity afterSource) {
        if (!Objects.equals(before.getSourceId(), after.getSourceId())) {
            return true;
        }
        if (!Objects.equals(trimNorm(before.getCollectionName()), trimNorm(after.getCollectionName()))) {
            return true;
        }
        String p =
                afterSource != null
                        ? StringUtils.defaultIfBlank(StringUtils.trimToNull(afterSource.getExtensionCode()), "")
                        : "";
        if (AiVecDriverEnum.Provider.QDRANT.getCode().equalsIgnoreCase(p)) {
            if (!Objects.equals(before.getDimension(), after.getDimension())) {
                return true;
            }
            if (!Objects.equals(trimNorm(before.getDistanceMetric()), trimNorm(after.getDistanceMetric()))) {
                return true;
            }
        }
        return false;
    }

    private static String trimNorm(String s) {
        return StringUtils.trimToNull(s);
    }

    @Override
    public BaseResponse<String> create(AiVecStoreCreateRequestDTO request) {
        AiVecStoreEntity entity = new AiVecStoreEntity();
        BeanUtils.copyProperties(request, entity);

        // Auto-create embedding instance from modelKey if instanceKey not provided
        if (StringUtils.isBlank(entity.getInstanceKey()) && StringUtils.isNotBlank(entity.getModelKey())) {
            String autoInstanceKey = "vec-" + entity.getModelKey() + "-" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            AiInstanceCreateRequestDTO instReq = new AiInstanceCreateRequestDTO();
            instReq.setInstanceKey(autoInstanceKey);
            instReq.setModelKey(entity.getModelKey());
            instReq.setInstanceName("vec-auto-" + entity.getModelKey());
            instReq.setStatus("enabled");
            aiInstanceService.create(instReq);
            entity.setInstanceKey(autoInstanceKey);
        }

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED);
        }
        AiVecStoreEntity persisted = getById(entity.getId());
        if (persisted == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED);
        }
        try {
            astroVecStorePhysicalOps.createPhysical(persisted);
        } catch (BusinessException e) {
            removeById(entity.getId());
            throw e;
        } catch (RuntimeException e) {
            removeById(entity.getId());
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED, e.getMessage());
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecStoreEntity snap = getById(id);
            if (snap != null) {
                astroVecStorePhysicalOps.dropPhysical(snap);
            }
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiVecStoreUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        AiVecStoreEntity before = getById(request.getId());
        if (before == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        AiVecStoreEntity after = new AiVecStoreEntity();
        BeanUtils.copyProperties(before, after);
        mergeVecStoreUpdate(after, request);

        AiVecSourceEntity afterSrc = aiVecSourceService.getById(after.getSourceId());
        if (afterSrc == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "向量数据源不存在");
        }
        boolean physical = physicalChanged(before, after, afterSrc);
        if (physical) {
            astroVecStorePhysicalOps.dropPhysical(before);
        }
        boolean result = updateById(after);
        if (!result) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_UPDATE_FAILED);
        }
        if (physical) {
            AiVecStoreEntity persisted = getById(request.getId());
            if (persisted != null) {
                astroVecStorePhysicalOps.createPhysical(persisted);
            }
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request) {
        IPage<AiVecStoreResponseDTO> page = PageUtils.buildPage(request);
        AiVecStoreQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecStoreQueryRequestDTO();
        }
        IPage<AiVecStoreResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecStoreResponseDTO> detail(Long id) {
        AiVecStoreResponseDTO responseDTO = baseMapper.selectDetailDtoById(id);
        if (responseDTO == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<AiVecStoreStatsResponseDTO> stats(Long id) {
        if (id == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AiVecStoreStatsResponseDTO stats = baseMapper.selectStoreStats(id, envCode);
        if (stats == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        if (stats.getDocCount() == null) {
            stats.setDocCount(0L);
        }
        if (stats.getSegmentCount() == null) {
            stats.setSegmentCount(0L);
        }
        if (stats.getTotalWordCount() == null) {
            stats.setTotalWordCount(0L);
        }

        // 物理层统计
        try {
            AiVecStoreEntity store = getById(id);
            if (store != null && store.getSourceId() != null) {
                VecSource vs = astroVecSourceFactory.tryGetActiveSource(store.getSourceId()).orElse(null);
                if (vs != null) {
                    VecStore vecStore = vs.openStore(store);
                    stats.setPhysicalVectorCount(vecStore.count());
                    stats.setCollectionExists(vecStore.exists());
                } else {
                    stats.setPhysicalVectorCount(-1L);
                    stats.setCollectionExists(false);
                }
            }
        } catch (Exception e) {
            log.warn("查询物理层统计失败 storeId={}", id, e);
            stats.setPhysicalVectorCount(-1L);
            stats.setCollectionExists(false);
        }

        return BaseResponse.success(stats);
    }
}
