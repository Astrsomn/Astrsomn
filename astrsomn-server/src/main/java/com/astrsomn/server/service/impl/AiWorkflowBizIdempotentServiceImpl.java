package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.commn.base.ErrorEnum;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.utils.PageConverter;
import com.astrsomn.core.common.utils.PageUtils;
import com.astrsomn.server.service.AiWorkflowBizIdempotentService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentCreateRequestDTO;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentQueryRequestDTO;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentResponseDTO;
import com.astrsomn.workflow.core.domain.dto.bizidempotent.AstFlowBizIdempotentUpdateRequestDTO;
import com.astrsomn.workflow.core.domain.entity.AstFlowBizIdempotentEntity;
import com.astrsomn.workflow.starter.mapper.AstFlowBizIdempotentMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiWorkflowBizIdempotentServiceImpl extends ServiceImpl<AstFlowBizIdempotentMapper, AstFlowBizIdempotentEntity>
        implements AiWorkflowBizIdempotentService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AstFlowBizIdempotentCreateRequestDTO request) {
        AstFlowBizIdempotentEntity entity = new AstFlowBizIdempotentEntity();
        entity.setIdempotentKey(request.getIdempotentKey());
        entity.setBizType(request.getBizType());
        entity.setBizId(request.getBizId());
        entity.setRequestHash(request.getRequestHash());
        entity.setResultRef(request.getResultRef());
        entity.setExpireAtMs(request.getExpireAtMs());
        boolean result = save(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "幂等记录创建失败");
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少待删除ID");
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "幂等记录删除失败");
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AstFlowBizIdempotentUpdateRequestDTO request) {
        if (request.getId() == null) throw new BusinessException(ErrorEnum.PARAM_ERROR, "缺少幂等记录ID");
        if (getById(request.getId()) == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "幂等记录不存在");
        AstFlowBizIdempotentEntity entity = new AstFlowBizIdempotentEntity();
        entity.setId(request.getId());
        entity.setIdempotentKey(request.getIdempotentKey());
        entity.setBizType(request.getBizType());
        entity.setBizId(request.getBizId());
        entity.setRequestHash(request.getRequestHash());
        entity.setResultRef(request.getResultRef());
        entity.setExpireAtMs(request.getExpireAtMs());
        boolean result = updateById(entity);
        if (!result) throw new BusinessException(ErrorEnum.BUSINESS_ERROR, "幂等记录更新失败");
        return BaseResponse.success("更新成功");
    }

    @Override
    public BaseResponse<AstFlowBizIdempotentResponseDTO> detail(Long id) {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        AstFlowBizIdempotentResponseDTO detail = baseMapper.detail(id, envCode);
        if (detail == null) throw new BusinessException(ErrorEnum.NOT_FOUND, "幂等记录不存在");
        return BaseResponse.success(detail);
    }

    @Override
    public PageResponse<AstFlowBizIdempotentResponseDTO> queryPage(BasePageRequest<AstFlowBizIdempotentQueryRequestDTO> request) {
        IPage<AstFlowBizIdempotentResponseDTO> page = PageUtils.buildPage(request);
        AstFlowBizIdempotentQueryRequestDTO param = request.getParam();
        if (param == null) param = new AstFlowBizIdempotentQueryRequestDTO();
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AstFlowBizIdempotentResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
