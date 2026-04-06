package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentCreateRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentQueryRequestDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentResponseDTO;
import org.astrsomn.core.common.dto.vecsegment.AiVecSegmentUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecSegmentEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecSegmentErrorEnum;
import org.astrsomn.core.mapper.AiVecSegmentMapper;
import org.astrsomn.server.service.AiVecSegmentService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiVecSegmentServiceImpl extends ServiceImpl<AiVecSegmentMapper, AiVecSegmentEntity> implements AiVecSegmentService {

    private final QueryEnvParamHelper queryEnvParamHelper;

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
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecSegmentErrorEnum.SEGMENT_PARAM_ERROR);
        }
        for (long id : ids) {
            removeById(id);
        }
        return BaseResponse.success("删除成功");
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
        IPage<AiVecSegmentResponseDTO> page = request.buildPage();
        AiVecSegmentQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecSegmentQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecSegmentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
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
