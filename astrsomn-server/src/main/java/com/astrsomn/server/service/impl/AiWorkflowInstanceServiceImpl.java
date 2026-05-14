package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceResponseDTO;
import com.astrsomn.api.workflow.domain.dto.instance.AstFlowInstanceUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowInstanceEntity;
import com.astrsomn.common.base.*;
import com.astrsomn.server.service.AiWorkflowInstanceService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.workflow.mapper.AstFlowInstanceMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowInstanceServiceImpl extends ServiceImpl<AstFlowInstanceMapper, AstFlowInstanceEntity>
        implements AiWorkflowInstanceService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowInstanceCreateRequestDTO request) {
        AstFlowInstanceEntity entity = new AstFlowInstanceEntity();
        entity.setDeploymentId(request.getDeploymentId());
        entity.setBusinessKey(request.getBusinessKey());
        entity.setExecutionStatus(request.getExecutionStatus());
        entity.setCurrentNodeId(request.getCurrentNodeId());
        entity.setStateJson(request.getStateJson());
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "流程实例创建失败");
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
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "流程实例删除失败");
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowInstanceUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少流程实例ID");
        }
        AstFlowInstanceEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "流程实例不存在");
        }
        AstFlowInstanceEntity entity = new AstFlowInstanceEntity();
        entity.setId(request.getId());
        entity.setDeploymentId(request.getDeploymentId());
        entity.setBusinessKey(request.getBusinessKey());
        entity.setExecutionStatus(request.getExecutionStatus());
        entity.setCurrentNodeId(request.getCurrentNodeId());
        entity.setStateJson(request.getStateJson());
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "流程实例更新失败");
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowInstanceResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowInstanceResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "流程实例不存在");
        }
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowInstanceResponseDTO> queryPage(BasePageRequest<AstFlowInstanceQueryRequestDTO> request) {
        IPage<AstFlowInstanceResponseDTO> page = PageUtils.buildPage(request);
        AstFlowInstanceQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AstFlowInstanceQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowInstanceResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
