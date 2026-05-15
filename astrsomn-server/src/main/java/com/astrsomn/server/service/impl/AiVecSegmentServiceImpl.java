package com.astrsomn.server.service.impl;

import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentResponseDTO;
import com.astrsomn.api.vector.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import com.astrsomn.api.vector.entity.AiVecSegmentEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecStore;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.exception.AstVecDocErrorEnum;
import com.astrsomn.api.vector.exception.AstVecSegmentErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.mapper.AiVecSegmentMapper;
import com.astrsomn.server.service.AiVecSegmentService;
import com.astrsomn.server.service.AiVecStoreService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.runtime.langchain.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiVecSegmentServiceImpl extends ServiceImpl<AiVecSegmentMapper, AiVecSegmentEntity> implements AiVecSegmentService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AiVecStoreService aiVecStoreService;
    private final AstroVecSourceFactory astroVecSourceFactory;

    @Override
    public BaseResponse<String> create(AiVecSegmentCreateRequestDTO request) {
        AiVecSegmentEntity entity = new AiVecSegmentEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        for (long id : ids) {
            AiVecSegmentEntity segment = getById(id);
            if (segment == null) {
                continue;
            }
            deleteVectorBySegment(segment);
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    private void deleteVectorBySegment(AiVecSegmentEntity segment) {
        if (segment.getCollectionId() == null || segment.getVectorId() == null || segment.getVectorId().isBlank()) {
            return;
        }
        AiVecStoreEntity store = aiVecStoreService.getById(segment.getCollectionId());
        if (store == null || store.getSourceId() == null) {
            return;
        }
        VecSource vecSource = astroVecSourceFactory.tryGetActiveSource(store.getSourceId())
                .orElseThrow(() -> new BusinessException(AstVecDocErrorEnum.DOC_SOURCE_NOT_READY));
        VecStore vecStore = vecSource.openStore(store);
        EmbeddingStore<TextSegment> embeddingStore = vecStore.getEmbeddingStore();
        try {
            embeddingStore.remove(segment.getVectorId());
        } catch (RuntimeException e) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_DELETE_FAILED, e.getMessage());
        }
    }

    @Override
    public BaseResponse<String> update(AiVecSegmentUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        AiVecSegmentEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        AiVecSegmentEntity entity = new AiVecSegmentEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecSegmentResponseDTO> queryPage(BasePageRequest<AiVecSegmentQueryRequestDTO> request) {
        IPage<AiVecSegmentResponseDTO> page = PageUtils.buildPage(request);
        AiVecSegmentQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecSegmentQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecSegmentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecSegmentResponseDTO> detail(Long id) {
        AiVecSegmentEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_NOT_FOUND);
        }
        AiVecSegmentResponseDTO responseDTO = new AiVecSegmentResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
