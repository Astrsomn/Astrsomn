package com.astrsomn.server.service.vector;

import com.astrsomn.api.runtime.common.constant.AiInstanceEnum;
import com.astrsomn.api.runtime.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecStore;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.dto.vecstore.*;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.vector.exception.AstVecStoreErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecStoreMapper;
import com.astrsomn.server.service.ai.AiInstanceService;

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


    private final VectorStorePhysicalHandler astroVecStorePhysicalOps;
    private final AiVecSourceService aiVecSourceService;
    private final AiInstanceService aiInstanceService;
    private final AstroVecSourceFactory astroVecSourceFactory;

    private static void mergeVecStoreUpdate(AiVecStoreEntity target, AiVecStoreUpdateRequestDTO req) {
        if (Objects.nonNull(req.getSourceId())) {
            target.setSourceId(req.getSourceId());
        }
        if (Objects.nonNull(req.getCollectionName())) {
            target.setCollectionName(req.getCollectionName());
        }
        if (Objects.nonNull(req.getDimension())) {
            target.setDimension(req.getDimension());
        }
        if (Objects.nonNull(req.getDistanceMetric())) {
            target.setDistanceMetric(req.getDistanceMetric());
        }
        if (Objects.nonNull(req.getMetadataSchema())) {
            target.setMetadataSchema(req.getMetadataSchema());
        }
        if (Objects.nonNull(req.getInstanceKey())) {
            target.setInstanceKey(req.getInstanceKey());
        }
        if (Objects.nonNull(req.getModelKey())) {
            target.setModelKey(req.getModelKey());
        }
        if (Objects.nonNull(req.getChunkStrategy())) {
            target.setChunkStrategy(req.getChunkStrategy());
        }
        if (Objects.nonNull(req.getChunkSize())) {
            target.setChunkSize(req.getChunkSize());
        }
        if (Objects.nonNull(req.getChunkOverlap())) {
            target.setChunkOverlap(req.getChunkOverlap());
        }
        if (Objects.nonNull(req.getDenseWeight())) {
            target.setDenseWeight(req.getDenseWeight());
        }
        if (Objects.nonNull(req.getInstructionPrefix())) {
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
            return !Objects.equals(trimNorm(before.getDistanceMetric()), trimNorm(after.getDistanceMetric()));
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
            instReq.setStatus(AiInstanceEnum.StatusEnum.ENABLED.getCode());
            if (StringUtils.isNotBlank(request.getAccountKey())) {
                instReq.setAccountKey(request.getAccountKey());
            }
            aiInstanceService.create(instReq);
            entity.setInstanceKey(autoInstanceKey);
        }

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED);
        }

        // 更新关联实例的 accountKey 和 bizKey
        String instanceKey = StringUtils.trimToNull(entity.getInstanceKey());
        String accountKey = StringUtils.trimToNull(request.getAccountKey());
        if (Objects.nonNull(instanceKey)) {
            AiInstanceEntity inst = aiInstanceService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiInstanceEntity>()
                            .eq(AiInstanceEntity::getInstanceKey, instanceKey)
                            .last("LIMIT 1"));
            if (Objects.nonNull(inst)) {
                boolean needUpdate = false;
                if (Objects.nonNull(accountKey) && !accountKey.equals(StringUtils.trimToNull(inst.getAccountKey()))) {
                    inst.setAccountKey(accountKey);
                    needUpdate = true;
                }
                String bizKeyVal = String.valueOf(entity.getId());
                if (!bizKeyVal.equals(StringUtils.trimToNull(inst.getBizKey()))) {
                    inst.setBizKey(bizKeyVal);
                    needUpdate = true;
                }
                if (needUpdate) {
                    aiInstanceService.updateById(inst);
                }
            }
        }

        AiVecStoreEntity persisted = getById(entity.getId());
        if (Objects.isNull(persisted)) {
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
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecStoreEntity snap = getById(id);
            if (Objects.nonNull(snap)) {
                astroVecStorePhysicalOps.dropPhysical(snap);
            }
            removeById(id);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> update(AiVecStoreUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        AiVecStoreEntity before = getById(request.getId());
        if (Objects.isNull(before)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        AiVecStoreEntity after = new AiVecStoreEntity();
        BeanUtils.copyProperties(before, after);
        mergeVecStoreUpdate(after, request);

        AiVecSourceEntity afterSrc = aiVecSourceService.getById(after.getSourceId());
        if (Objects.isNull(afterSrc)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR, "Vector data source not found");
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
            if (Objects.nonNull(persisted)) {
                astroVecStorePhysicalOps.createPhysical(persisted);
            }
        }

        String accountKey = StringUtils.trimToNull(request.getAccountKey());
        String instanceKey = StringUtils.trimToNull(after.getInstanceKey());
        if (Objects.nonNull(accountKey) && Objects.nonNull(instanceKey)) {
            AiInstanceEntity inst = aiInstanceService.getOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiInstanceEntity>()
                            .eq(AiInstanceEntity::getInstanceKey, instanceKey)
                            .last("LIMIT 1"));
            if (Objects.nonNull(inst) && !accountKey.equals(StringUtils.trimToNull(inst.getAccountKey()))) {
                inst.setAccountKey(accountKey);
                aiInstanceService.updateById(inst);
            }
        }

        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request) {
        IPage<AiVecStoreResponseDTO> page = PageUtils.buildPage(request);
        AiVecStoreQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiVecStoreQueryRequestDTO();
        }
        IPage<AiVecStoreResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecStoreResponseDTO> detail(Long id) {
        AiVecStoreResponseDTO responseDTO = baseMapper.selectDetailDtoById(id);
        if (Objects.isNull(responseDTO)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<AiVecStoreStatsResponseDTO> stats(Long id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }

        AiVecStoreStatsResponseDTO stats = baseMapper.selectStoreStats(id);
        if (Objects.isNull(stats)) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        if (Objects.isNull(stats.getDocCount())) {
            stats.setDocCount(0L);
        }
        if (Objects.isNull(stats.getSegmentCount())) {
            stats.setSegmentCount(0L);
        }
        if (Objects.isNull(stats.getTotalWordCount())) {
            stats.setTotalWordCount(0L);
        }

        try {
            AiVecStoreEntity store = getById(id);
            if (Objects.nonNull(store) && Objects.nonNull(store.getSourceId())) {
                VecSource vs = astroVecSourceFactory.tryGetActiveSource(store.getSourceId()).orElse(null);
                if (Objects.nonNull(vs)) {
                    VecStore vecStore = vs.openStore(store);
                    stats.setPhysicalVectorCount(vecStore.count());
                    stats.setCollectionExists(vecStore.exists());
                } else {
                    stats.setPhysicalVectorCount(-1L);
                    stats.setCollectionExists(false);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to query physical layer stats storeId={}", id, e);
            stats.setPhysicalVectorCount(-1L);
            stats.setCollectionExists(false);
        }

        return BaseResponse.success(stats);
    }
}
