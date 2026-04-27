package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageCreateRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiChatMessageEntity;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiChatErrorEnum;
import com.astrsomn.starter.mapper.AiChatMessageMapper;
import com.astrsomn.server.service.AiChatMessageService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.astrsomn.core.common.utils.PageUtils;
import java.util.Arrays;
import java.util.List;
@Service
@RequiredArgsConstructor
public class AiChatMessageServiceImpl extends ServiceImpl<AiChatMessageMapper, AiChatMessageEntity> implements AiChatMessageService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    @Override
    public BaseResponse<String> create(AiChatMessageCreateRequestDTO request) {
        AiChatMessageEntity entity = new AiChatMessageEntity();
    
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
    public BaseResponse<AiChatMessageResponseDTO> detail(Long id) {
        AiChatMessageEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiChatErrorEnum.CHAT_NOT_FOUND);
        }
        AiChatMessageResponseDTO responseDTO = new AiChatMessageResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiChatMessageUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiChatErrorEnum.CHAT_PARAM_ERROR);
        }
        AiChatMessageEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiChatErrorEnum.CHAT_NOT_FOUND);
        }
        AiChatMessageEntity entity = new AiChatMessageEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiChatErrorEnum.CHAT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiChatMessageResponseDTO> queryPage(BasePageRequest<AiChatMessageQueryRequestDTO> request) {
        IPage<AiChatMessageResponseDTO> page = PageUtils.buildPage(request);
        AiChatMessageQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiChatMessageQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiChatMessageResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public PageResponse<AiChatMessageResponseDTO> queryGroups(BasePageRequest<AiChatMessageQueryRequestDTO> request) {
        IPage<AiChatMessageResponseDTO> page = PageUtils.buildPage(request);
        AiChatMessageQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiChatMessageQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiChatMessageResponseDTO> result = baseMapper.queryGroups(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<List<AiChatMessageResponseDTO>> recoverByMemoryKey(String memoryKey) {
        if (memoryKey == null || memoryKey.isEmpty()) {
            throw new BusinessException(AiChatErrorEnum.CHAT_PARAM_ERROR);
        }
        
        List<AiChatMessageResponseDTO> responseDTOs = baseMapper.recoverByMemoryKey(memoryKey);
        if (responseDTOs == null || responseDTOs.isEmpty()) {
            throw new BusinessException(AiChatErrorEnum.CHAT_NOT_FOUND);
        }
        
        return BaseResponse.success(responseDTOs);
    }
}
