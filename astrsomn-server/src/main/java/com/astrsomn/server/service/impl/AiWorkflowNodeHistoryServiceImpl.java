package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.commn.base.ErrorEnum;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.utils.PageConverter;
import com.astrsomn.core.common.utils.PageUtils;
import com.astrsomn.server.service.AiWorkflowNodeHistoryService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryResponseDTO;
import com.astrsomn.workflow.core.domain.dto.nodehistory.AstFlowNodeHistoryUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowNodeHistoryEntity;
import com.astrsomn.workflow.starter.mapper.AstFlowNodeHistoryMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowNodeHistoryServiceImpl extends ServiceImpl<AstFlowNodeHistoryMapper, AstFlowNodeHistoryEntity>
        implements AiWorkflowNodeHistoryService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowNodeHistoryCreateRequestDTO request) {
        AstFlowNodeHistoryEntity entity = new AstFlowNodeHistoryEntity();
        entity.setInstanceId(request.getInstanceId());
        entity.setNodeId(request.getNodeId());
        entity.setInputData(request.getInputData());
        entity.setOutputData(request.getOutputData());
        entity.setExecutionMs(request.getExecutionMs());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点历史创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点历史删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowNodeHistoryUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少节点历史ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "节点历史不存在");
        AstFlowNodeHistoryEntity entity = new AstFlowNodeHistoryEntity();
        entity.setId(request.getId());
        entity.setInstanceId(request.getInstanceId());
        entity.setNodeId(request.getNodeId());
        entity.setInputData(request.getInputData());
        entity.setOutputData(request.getOutputData());
        entity.setExecutionMs(request.getExecutionMs());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "节点历史更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowNodeHistoryResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowNodeHistoryResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "节点历史不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowNodeHistoryResponseDTO> queryPage(BasePageRequest<AstFlowNodeHistoryQueryRequestDTO> request) {
        IPage<AstFlowNodeHistoryResponseDTO> page = PageUtils.buildPage(request);
        AstFlowNodeHistoryQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowNodeHistoryQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowNodeHistoryResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
