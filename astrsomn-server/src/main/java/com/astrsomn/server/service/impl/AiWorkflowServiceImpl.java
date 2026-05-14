package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionResponseDTO;
import com.astrsomn.api.workflow.domain.dto.definition.AstFlowDefinitionUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowDefinitionEntity;
import com.astrsomn.common.base.*;
import com.astrsomn.server.service.AiWorkflowService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.workflow.mapper.AstFlowDefinitionMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowServiceImpl extends ServiceImpl<AstFlowDefinitionMapper, AstFlowDefinitionEntity> implements AiWorkflowService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowDefinitionCreateRequestDTO request) {
        AstFlowDefinitionEntity entity = new AstFlowDefinitionEntity();
        entity.setFlowKey(request.getWorkflowKey());
        entity.setName(request.getWorkflowName());
        entity.setCategory(request.getDescription());
        entity.setDraftGraphJson(request.getGraphJson());
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "工作流创建失败");
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
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "工作流删除失败");
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowDefinitionUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少工作流ID");
        }
        AstFlowDefinitionEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "工作流不存在");
        }
        AstFlowDefinitionEntity entity = new AstFlowDefinitionEntity();
        entity.setId(request.getId());
        entity.setFlowKey(request.getWorkflowKey());
        entity.setName(request.getWorkflowName());
        entity.setCategory(request.getDescription());
        entity.setDraftGraphJson(request.getGraphJson());
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "工作流更新失败");
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowDefinitionResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowDefinitionResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) {
            throw new BusinessException(ErrorEnum.NOT_FOUND, "工作流不存在");
        }
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowDefinitionResponseDTO> queryPage(BasePageRequest<AstFlowDefinitionQueryRequestDTO> request) {
        IPage<AstFlowDefinitionResponseDTO> page = PageUtils.buildPage(request);
        AstFlowDefinitionQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AstFlowDefinitionQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowDefinitionResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
