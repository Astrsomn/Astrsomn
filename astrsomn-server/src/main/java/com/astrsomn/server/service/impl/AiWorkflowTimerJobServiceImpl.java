package com.astrsomn.server.service.impl;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.ErrorEnum;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.server.service.AiWorkflowTimerJobService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobResponseDTO;
import com.astrsomn.api.workflow.domain.dto.timerjob.AstFlowTimerJobUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowTimerJobEntity;
import com.astrsomn.starter.workflow.mapper.AstFlowTimerJobMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowTimerJobServiceImpl extends ServiceImpl<AstFlowTimerJobMapper, AstFlowTimerJobEntity>
        implements AiWorkflowTimerJobService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowTimerJobCreateRequestDTO request) {
        AstFlowTimerJobEntity entity = new AstFlowTimerJobEntity();
        entity.setInstanceId(request.getInstanceId());
        entity.setNodeId(request.getNodeId());
        entity.setJobType(request.getJobType());
        entity.setDueTimeMs(request.getDueTimeMs());
        entity.setJobStatus(request.getJobStatus());
        entity.setRetryCount(request.getRetryCount());
        entity.setMaxRetry(request.getMaxRetry());
        entity.setLastError(request.getLastError());
        entity.setPayloadJson(request.getPayloadJson());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "定时任务创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "定时任务删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowTimerJobUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少定时任务ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "定时任务不存在");
        AstFlowTimerJobEntity entity = new AstFlowTimerJobEntity();
        entity.setId(request.getId());
        entity.setInstanceId(request.getInstanceId());
        entity.setNodeId(request.getNodeId());
        entity.setJobType(request.getJobType());
        entity.setDueTimeMs(request.getDueTimeMs());
        entity.setJobStatus(request.getJobStatus());
        entity.setRetryCount(request.getRetryCount());
        entity.setMaxRetry(request.getMaxRetry());
        entity.setLastError(request.getLastError());
        entity.setPayloadJson(request.getPayloadJson());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "定时任务更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowTimerJobResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowTimerJobResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "定时任务不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowTimerJobResponseDTO> queryPage(BasePageRequest<AstFlowTimerJobQueryRequestDTO> request) {
        IPage<AstFlowTimerJobResponseDTO> page = PageUtils.buildPage(request);
        AstFlowTimerJobQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowTimerJobQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowTimerJobResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
