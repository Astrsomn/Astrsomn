package com.astrsomn.server.service.impl;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.ErrorEnum;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.server.service.AiWorkflowHumanTaskService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskResponseDTO;
import com.astrsomn.api.workflow.domain.dto.humantask.AstFlowHumanTaskUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowHumanTaskEntity;
import com.astrsomn.starter.workflow.mapper.AstFlowHumanTaskMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowHumanTaskServiceImpl extends ServiceImpl<AstFlowHumanTaskMapper, AstFlowHumanTaskEntity>
        implements AiWorkflowHumanTaskService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowHumanTaskCreateRequestDTO request) {
        AstFlowHumanTaskEntity entity = new AstFlowHumanTaskEntity();
        entity.setInstanceId(request.getInstanceId());
        entity.setNodeId(request.getNodeId());
        entity.setTaskStatus(request.getTaskStatus());
        entity.setPayload(request.getPayload());
        entity.setActionData(request.getActionData());
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "人工任务创建失败");
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        }
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "人工任务删除失败");
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowHumanTaskUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少人工任务ID");
        }
        AstFlowHumanTaskEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "人工任务不存在");
        }
        AstFlowHumanTaskEntity entity = new AstFlowHumanTaskEntity();
        entity.setId(request.getId());
        entity.setInstanceId(request.getInstanceId());
        entity.setNodeId(request.getNodeId());
        entity.setTaskStatus(request.getTaskStatus());
        entity.setPayload(request.getPayload());
        entity.setActionData(request.getActionData());
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "人工任务更新失败");
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowHumanTaskResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowHumanTaskResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "人工任务不存在");
        }
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowHumanTaskResponseDTO> queryPage(BasePageRequest<AstFlowHumanTaskQueryRequestDTO> request) {
        IPage<AstFlowHumanTaskResponseDTO> page = PageUtils.buildPage(request);
        AstFlowHumanTaskQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AstFlowHumanTaskQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowHumanTaskResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
