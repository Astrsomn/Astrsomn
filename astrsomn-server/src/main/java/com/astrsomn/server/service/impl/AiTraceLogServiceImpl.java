package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogCreateRequestDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.core.common.dto.tracelog.AiTraceLogUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiTraceLogEntity;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiTraceLogErrorEnum;
import com.astrsomn.starter.mapper.AiTraceLogMapper;
import com.astrsomn.server.service.AiTraceLogService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.astrsomn.core.common.utils.PageUtils;
@Service
@RequiredArgsConstructor
public class AiTraceLogServiceImpl extends ServiceImpl<AiTraceLogMapper, AiTraceLogEntity> implements AiTraceLogService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiTraceLogCreateRequestDTO request) {
        AiTraceLogEntity entity = new AiTraceLogEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_PARAM_ERROR);
        }
        for (long id : ids) {
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiTraceLogUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_PARAM_ERROR);
        }
        AiTraceLogEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_NOT_FOUND);
        }
        AiTraceLogEntity entity = new AiTraceLogEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiTraceLogResponseDTO> queryPage(BasePageRequest<AiTraceLogQueryRequestDTO> request) {
        IPage<AiTraceLogResponseDTO> page = PageUtils.buildPage(request);
        AiTraceLogQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiTraceLogQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiTraceLogResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiTraceLogResponseDTO> detail(Long id) {
        AiTraceLogEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_NOT_FOUND);
        }
        AiTraceLogResponseDTO responseDTO = new AiTraceLogResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
