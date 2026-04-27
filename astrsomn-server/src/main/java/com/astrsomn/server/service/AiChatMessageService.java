package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageCreateRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.core.common.dto.chat.message.AiChatMessageUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiChatMessageEntity;

import java.util.List;

public interface AiChatMessageService extends IService<AiChatMessageEntity> {
    BaseResponse<String> create(AiChatMessageCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiChatMessageResponseDTO> detail(Long id);

    BaseResponse<String> update(AiChatMessageUpdateRequestDTO request);

    PageResponse<AiChatMessageResponseDTO> queryPage(BasePageRequest<AiChatMessageQueryRequestDTO> request);

    PageResponse<AiChatMessageResponseDTO> queryGroups(BasePageRequest<AiChatMessageQueryRequestDTO> request);

    BaseResponse<List<AiChatMessageResponseDTO>> recoverByMemoryKey(String memoryKey);
}
