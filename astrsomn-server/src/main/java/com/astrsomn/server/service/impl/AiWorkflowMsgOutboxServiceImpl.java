package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxCreateRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxQueryRequestDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxResponseDTO;
import com.astrsomn.api.workflow.domain.dto.msgoutbox.AstFlowMsgOutboxUpdateRequestDTO;
import com.astrsomn.api.workflow.domain.entity.AstFlowMsgOutboxEntity;
import com.astrsomn.common.base.*;
import com.astrsomn.server.service.AiWorkflowMsgOutboxService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.workflow.mapper.AstFlowMsgOutboxMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowMsgOutboxServiceImpl extends ServiceImpl<AstFlowMsgOutboxMapper, AstFlowMsgOutboxEntity>
        implements AiWorkflowMsgOutboxService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowMsgOutboxCreateRequestDTO request) {
        AstFlowMsgOutboxEntity entity = new AstFlowMsgOutboxEntity();
        entity.setBizType(request.getBizType());
        entity.setBizId(request.getBizId());
        entity.setTopicOrEndpoint(request.getTopicOrEndpoint());
        entity.setPayloadJson(request.getPayloadJson());
        entity.setMsgStatus(request.getMsgStatus());
        entity.setRetryCount(request.getRetryCount());
        entity.setNextRetryTimeMs(request.getNextRetryTimeMs());
        entity.setLastError(request.getLastError());
        entity.setIdempotentKey(request.getIdempotentKey());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "消息外发记录创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "消息外发记录删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowMsgOutboxUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少消息外发记录ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "消息外发记录不存在");
        AstFlowMsgOutboxEntity entity = new AstFlowMsgOutboxEntity();
        entity.setId(request.getId());
        entity.setBizType(request.getBizType());
        entity.setBizId(request.getBizId());
        entity.setTopicOrEndpoint(request.getTopicOrEndpoint());
        entity.setPayloadJson(request.getPayloadJson());
        entity.setMsgStatus(request.getMsgStatus());
        entity.setRetryCount(request.getRetryCount());
        entity.setNextRetryTimeMs(request.getNextRetryTimeMs());
        entity.setLastError(request.getLastError());
        entity.setIdempotentKey(request.getIdempotentKey());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "消息外发记录更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowMsgOutboxResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowMsgOutboxResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "消息外发记录不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowMsgOutboxResponseDTO> queryPage(BasePageRequest<AstFlowMsgOutboxQueryRequestDTO> request) {
        IPage<AstFlowMsgOutboxResponseDTO> page = PageUtils.buildPage(request);
        AstFlowMsgOutboxQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowMsgOutboxQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowMsgOutboxResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
