package com.astrsomn.server.service.workflow;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigResponseDTO;
import com.astrsomn.api.workflow.domain.dto.nodeconfig.AstFlowNodeConfigUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowNodeConfigEntity;
import com.astrsomn.common.base.*;
import com.astrsomn.starter.workflow.mapper.AstFlowNodeConfigMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowNodeConfigServiceImpl extends ServiceImpl<AstFlowNodeConfigMapper, AstFlowNodeConfigEntity> implements AiWorkflowNodeConfigService {


    @Override
    public BaseResponse<String> create(AstFlowNodeConfigCreateRequestDTO request) {
        AstFlowNodeConfigEntity entity = new AstFlowNodeConfigEntity();
        entity.setFlowDefinitionId(request.getFlowDefinitionId());
        entity.setNodeId(request.getNodeId());
        entity.setNodeType(request.getNodeType());
        entity.setConfigJson(request.getConfigJson());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点配置创建失败");
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点配置删除失败");
        return BaseResponse.success("success");
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
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<AstFlowNodeConfigResponseDTO> detail(Long id) {
        AstFlowNodeConfigResponseDTO detail = baseMapper.detail(id);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "节点配置不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowNodeConfigResponseDTO> queryPage(BasePageRequest<AstFlowNodeConfigQueryRequestDTO> request) {
        IPage<AstFlowNodeConfigResponseDTO> page = PageUtils.buildPage(request);
        AstFlowNodeConfigQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowNodeConfigQueryRequestDTO();
        IPage<AstFlowNodeConfigResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
