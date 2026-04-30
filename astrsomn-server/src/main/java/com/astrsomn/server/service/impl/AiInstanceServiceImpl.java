package com.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.instance.AiInstanceCreateRequestDTO;
import com.astrsomn.core.common.dto.instance.AiInstanceQueryRequestDTO;
import com.astrsomn.core.common.dto.instance.AiInstanceResponseDTO;
import com.astrsomn.core.common.dto.instance.AiInstanceUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiInstanceEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiInstanceErrorEnum;
import com.astrsomn.starter.mapper.AiInstanceMapper;
import com.astrsomn.server.service.AiInstanceService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.astrsomn.core.common.utils.PageUtils;
import java.util.Arrays;
import com.astrsomn.core.common.utils.PageConverter;
@Service
@RequiredArgsConstructor
public class AiInstanceServiceImpl extends ServiceImpl<AiInstanceMapper, AiInstanceEntity> implements AiInstanceService {


    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiInstanceCreateRequestDTO request) {
        if (StringUtils.isBlank(request.getModelKey())) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_PARAM_ERROR);
        }
        AiInstanceEntity entity = new AiInstanceEntity();
        BeanUtils.copyProperties(request, entity);

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiInstanceResponseDTO> detail(Long id) {
        AiInstanceResponseDTO dto = baseMapper.selectDetailDtoById(id);
        if (dto == null) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_NOT_FOUND);
        }
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> update(AiInstanceUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_PARAM_ERROR);
        }
        AiInstanceEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_NOT_FOUND);
        }
        AiInstanceEntity entity = new AiInstanceEntity();
        BeanUtils.copyProperties(request, entity);

        entity.setModelKey(existing.getModelKey());
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiInstanceErrorEnum.INSTANCE_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiInstanceResponseDTO> queryPage(BasePageRequest<AiInstanceQueryRequestDTO> request) {
        IPage<AiInstanceResponseDTO> page = PageUtils.buildPage(request);
        AiInstanceQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiInstanceQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiInstanceResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }
}
