package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.constant.AiVecDriverEnum;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreCreateRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreResponseDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecStoreErrorEnum;
import org.astrsomn.core.mapper.AiVecStoreMapper;
import org.astrsomn.server.service.AiVecSourceService;
import org.astrsomn.server.service.AiVecStoreService;
import org.astrsomn.server.service.vector.VectorStorePhysicalHandler;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AiVecStoreServiceImpl extends ServiceImpl<AiVecStoreMapper, AiVecStoreEntity> implements AiVecStoreService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final VectorStorePhysicalHandler astroVecStorePhysicalOps;
    private final AiVecSourceService aiVecSourceService;

    @Override
    public BaseResponse<String> create(AiVecStoreCreateRequestDTO request) {
        AiVecStoreEntity entity = new AiVecStoreEntity();
        BeanUtils.copyProperties(request, entity);
        queryEnvParamHelper.stampEffectiveEnv(entity);
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
        queryEnvParamHelper.stampEffectiveEnv(after);

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
                        ? StringUtils.defaultIfBlank(StringUtils.trimToNull(afterSource.getProvider()), "")
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
    public PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request) {
        IPage<AiVecStoreResponseDTO> page = request.buildPage();
        AiVecStoreQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecStoreQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecStoreResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<AiVecStoreResponseDTO> detail(Long id) {
        AiVecStoreResponseDTO responseDTO = baseMapper.selectDetailDtoById(id);
        if (responseDTO == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        return BaseResponse.success(responseDTO);
    }
}
