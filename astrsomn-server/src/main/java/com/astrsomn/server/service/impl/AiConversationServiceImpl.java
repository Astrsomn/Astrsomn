package com.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.conversation.AiConversationCreateRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiConversationEntity;
import com.astrsomn.core.exception.base.BusinessException;
import com.astrsomn.core.exception.constant.AiConversationErrorEnum;
import com.astrsomn.core.mapper.AiConversationMapper;
import com.astrsomn.server.service.AiConversationService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversationEntity> implements AiConversationService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    @Override
    public BaseResponse<String> create(AiConversationCreateRequestDTO request) {
        AiConversationEntity entity = new AiConversationEntity();
    
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiConversationResponseDTO> detail(Long id) {
        AiConversationEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_NOT_FOUND);
        }
        AiConversationResponseDTO responseDTO = new AiConversationResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiConversationUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_PARAM_ERROR);
        }
        AiConversationEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_NOT_FOUND);
        }
        AiConversationEntity entity = new AiConversationEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiConversationResponseDTO> queryPage(BasePageRequest<AiConversationQueryRequestDTO> request) {
        IPage<AiConversationResponseDTO> page = request.buildPage();
        AiConversationQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiConversationQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiConversationResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public PageResponse<AiConversationResponseDTO> queryGroups(BasePageRequest<AiConversationQueryRequestDTO> request) {
        IPage<AiConversationResponseDTO> page = request.buildPage();
        AiConversationQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiConversationQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiConversationResponseDTO> result = baseMapper.queryGroups(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<List<AiConversationResponseDTO>> recoverByMemoryKey(String memoryKey) {
        if (memoryKey == null || memoryKey.isEmpty()) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_PARAM_ERROR);
        }
        
        List<AiConversationResponseDTO> responseDTOs = baseMapper.recoverByMemoryKey(memoryKey);
        if (responseDTOs == null || responseDTOs.isEmpty()) {
            throw new BusinessException(AiConversationErrorEnum.CONVERSATION_NOT_FOUND);
        }
        
        return BaseResponse.success(responseDTOs);
    }
}
