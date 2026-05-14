package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventResponseDTO;
import com.astrsomn.api.workflow.domain.dto.instanceevent.AstFlowInstanceEventUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowInstanceEventEntity;
import com.astrsomn.common.base.*;
import com.astrsomn.server.service.AiWorkflowInstanceEventService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.workflow.mapper.AstFlowInstanceEventMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowInstanceEventServiceImpl extends ServiceImpl<AstFlowInstanceEventMapper, AstFlowInstanceEventEntity>
        implements AiWorkflowInstanceEventService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowInstanceEventCreateRequestDTO request) {
        AstFlowInstanceEventEntity entity = new AstFlowInstanceEventEntity();
        entity.setInstanceId(request.getInstanceId());
        entity.setEventType(request.getEventType());
        entity.setNodeId(request.getNodeId());
        entity.setEventTimeMs(request.getEventTimeMs());
        entity.setEventDataJson(request.getEventDataJson());
        entity.setTraceId(request.getTraceId());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "实例事件创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "实例事件删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowInstanceEventUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少实例事件ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "实例事件不存在");
        AstFlowInstanceEventEntity entity = new AstFlowInstanceEventEntity();
        entity.setId(request.getId());
        entity.setInstanceId(request.getInstanceId());
        entity.setEventType(request.getEventType());
        entity.setNodeId(request.getNodeId());
        entity.setEventTimeMs(request.getEventTimeMs());
        entity.setEventDataJson(request.getEventDataJson());
        entity.setTraceId(request.getTraceId());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "实例事件更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowInstanceEventResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowInstanceEventResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "实例事件不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowInstanceEventResponseDTO> queryPage(BasePageRequest<AstFlowInstanceEventQueryRequestDTO> request) {
        IPage<AstFlowInstanceEventResponseDTO> page = PageUtils.buildPage(request);
        AstFlowInstanceEventQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowInstanceEventQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowInstanceEventResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
