package com.astrsomn.server.service.impl;

import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.ErrorEnum;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.server.service.AiWorkflowDeploymentService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentResponseDTO;
import com.astrsomn.api.workflow.domain.dto.deployment.AstFlowDeploymentUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowDeploymentEntity;
import com.astrsomn.starter.workflow.mapper.AstFlowDeploymentMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowDeploymentServiceImpl extends ServiceImpl<AstFlowDeploymentMapper, AstFlowDeploymentEntity>
        implements AiWorkflowDeploymentService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowDeploymentCreateRequestDTO request) {
        AstFlowDeploymentEntity entity = new AstFlowDeploymentEntity();
        entity.setFlowDefinitionId(request.getFlowDefinitionId());
        entity.setVersion(request.getVersion());
        entity.setDeployedGraphJson(request.getDeployedGraphJson());
        entity.setLatest(request.getLatest());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "流程部署创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "流程部署删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowDeploymentUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少流程部署ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "流程部署不存在");
        AstFlowDeploymentEntity entity = new AstFlowDeploymentEntity();
        entity.setId(request.getId());
        entity.setFlowDefinitionId(request.getFlowDefinitionId());
        entity.setVersion(request.getVersion());
        entity.setDeployedGraphJson(request.getDeployedGraphJson());
        entity.setLatest(request.getLatest());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "流程部署更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowDeploymentResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowDeploymentResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "流程部署不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowDeploymentResponseDTO> queryPage(BasePageRequest<AstFlowDeploymentQueryRequestDTO> request) {
        IPage<AstFlowDeploymentResponseDTO> page = PageUtils.buildPage(request);
        AstFlowDeploymentQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowDeploymentQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowDeploymentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
