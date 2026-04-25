package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.constant.SystemMessageEnum;
import com.astrsomn.core.common.dto.systemmessage.SystemMessageCreateRequestDTO;
import com.astrsomn.core.common.dto.systemmessage.SystemMessageQueryRequestDTO;
import com.astrsomn.core.common.dto.systemmessage.SystemMessageResponseDTO;
import com.astrsomn.core.common.dto.systemmessage.SystemMessageUpdateRequestDTO;
import com.astrsomn.core.common.entity.SystemMessageEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.SystemMessageErrorEnum;
import com.astrsomn.starter.mapper.SystemMessageMapper;
import com.astrsomn.server.service.SystemMessageService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.astrsomn.core.common.utils.PageUtils;
@Service
@RequiredArgsConstructor
public class SystemMessageServiceImpl extends ServiceImpl<SystemMessageMapper, SystemMessageEntity> implements
        SystemMessageService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(SystemMessageCreateRequestDTO request) {
        SystemMessageEntity entity = new SystemMessageEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getReadStatus())) {
            entity.setReadStatus(SystemMessageEnum.ReadStatusEnum.UNREAD.getCode());
        }
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(SystemMessageErrorEnum.SYSTEM_MESSAGE_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(SystemMessageErrorEnum.SYSTEM_MESSAGE_PARAM_ERROR);
        }
        for (long id : ids) {
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(SystemMessageUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(SystemMessageErrorEnum.SYSTEM_MESSAGE_PARAM_ERROR);
        }
        SystemMessageEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(SystemMessageErrorEnum.SYSTEM_MESSAGE_NOT_FOUND);
        }
        SystemMessageEntity entity = new SystemMessageEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(SystemMessageErrorEnum.SYSTEM_MESSAGE_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<SystemMessageResponseDTO> queryPage(BasePageRequest<SystemMessageQueryRequestDTO> request) {
        IPage<SystemMessageResponseDTO> page = PageUtils.buildPage(request);
        SystemMessageQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new SystemMessageQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<SystemMessageResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<SystemMessageResponseDTO> detail(Long id) {
        SystemMessageEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(SystemMessageErrorEnum.SYSTEM_MESSAGE_NOT_FOUND);
        }
        SystemMessageResponseDTO responseDTO = new SystemMessageResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
