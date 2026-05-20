package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogResponseDTO;
import com.astrsomn.api.runtime.common.dto.tracelog.AiTraceLogUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiTraceLogEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiTraceLogErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.server.mapper.AiTraceLogMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AiTraceLogServiceImpl extends ServiceImpl<AiTraceLogMapper, AiTraceLogEntity> implements AiTraceLogService {


    @Override
    public BaseResponse<String> create(AiTraceLogCreateRequestDTO request) {
        AiTraceLogEntity entity = new AiTraceLogEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_PARAM_ERROR);
        }
        Arrays.stream(ids).forEach(this::removeById);
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> update(AiTraceLogUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_PARAM_ERROR);
        }
        AiTraceLogEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_NOT_FOUND);
        }
        AiTraceLogEntity entity = new AiTraceLogEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiTraceLogResponseDTO> queryPage(BasePageRequest<AiTraceLogQueryRequestDTO> request) {
        IPage<AiTraceLogResponseDTO> page = PageUtils.buildPage(request);
        AiTraceLogQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiTraceLogQueryRequestDTO();
        }
        IPage<AiTraceLogResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiTraceLogResponseDTO> detail(Long id) {
        AiTraceLogEntity entity = getById(id);
        if (Objects.isNull(entity)) {
            throw new BusinessException(AiTraceLogErrorEnum.TRACE_LOG_NOT_FOUND);
        }
        AiTraceLogResponseDTO responseDTO = new AiTraceLogResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
