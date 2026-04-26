package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.commn.base.ErrorEnum;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.utils.PageConverter;
import com.astrsomn.core.common.utils.PageUtils;
import com.astrsomn.server.service.AiWorkflowNodeConfigService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigResponseDTO;
import com.astrsomn.workflow.core.domain.dto.nodeconfig.AstFlowNodeConfigUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowNodeConfigEntity;
import com.astrsomn.workflow.starter.mapper.AstFlowNodeConfigMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowNodeConfigServiceImpl extends ServiceImpl<AstFlowNodeConfigMapper, AstFlowNodeConfigEntity>
        implements AiWorkflowNodeConfigService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowNodeConfigCreateRequestDTO request) {
        AstFlowNodeConfigEntity entity = new AstFlowNodeConfigEntity();
        entity.setFlowDefinitionId(request.getFlowDefinitionId());
        entity.setNodeId(request.getNodeId());
        entity.setNodeType(request.getNodeType());
        entity.setConfigJson(request.getConfigJson());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点配置创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点配置删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowNodeConfigUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少节点配置ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "节点配置不存在");
        AstFlowNodeConfigEntity entity = new AstFlowNodeConfigEntity();
        entity.setId(request.getId());
        entity.setFlowDefinitionId(request.getFlowDefinitionId());
        entity.setNodeId(request.getNodeId());
        entity.setNodeType(request.getNodeType());
        entity.setConfigJson(request.getConfigJson());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点配置更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowNodeConfigResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowNodeConfigResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "节点配置不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowNodeConfigResponseDTO> queryPage(BasePageRequest<AstFlowNodeConfigQueryRequestDTO> request) {
        IPage<AstFlowNodeConfigResponseDTO> page = PageUtils.buildPage(request);
        AstFlowNodeConfigQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowNodeConfigQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowNodeConfigResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
