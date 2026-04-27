package com.astrsomn.server.service.impl;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionCreateRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiChatSessionEntity;
import com.astrsomn.core.common.utils.PageConverter;
import com.astrsomn.core.common.utils.PageUtils;
import com.astrsomn.core.exception.AiChatErrorEnum;
import com.astrsomn.server.service.AiChatSessionService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.mapper.AiChatSessionMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiChatSessionServiceImpl extends ServiceImpl<AiChatSessionMapper, AiChatSessionEntity>
        implements AiChatSessionService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiChatSessionCreateRequestDTO request) {
        AiChatSessionEntity entity = new AiChatSessionEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiChatErrorEnum.CHAT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiChatErrorEnum.CHAT_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiChatSessionUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiChatErrorEnum.CHAT_PARAM_ERROR);
        }
        AiChatSessionEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiChatErrorEnum.CHAT_NOT_FOUND);
        }
        AiChatSessionEntity entity = new AiChatSessionEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiChatErrorEnum.CHAT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiChatSessionResponseDTO> queryPage(BasePageRequest<AiChatSessionQueryRequestDTO> request) {
        IPage<AiChatSessionResponseDTO> page = PageUtils.buildPage(request);
        AiChatSessionQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiChatSessionQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiChatSessionResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiChatSessionResponseDTO> detail(Long id) {
        AiChatSessionEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiChatErrorEnum.CHAT_NOT_FOUND);
        }
        AiChatSessionResponseDTO responseDTO = new AiChatSessionResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
