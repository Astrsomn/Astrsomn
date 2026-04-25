package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.conversation.AiConversationCreateRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import com.astrsomn.core.common.dto.conversation.AiConversationUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiConversationEntity;

import java.util.List;

public interface AiConversationService extends IService<AiConversationEntity> {
    BaseResponse<String> create(AiConversationCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiConversationResponseDTO> detail(Long id);

    BaseResponse<String> update(AiConversationUpdateRequestDTO request);

    PageResponse<AiConversationResponseDTO> queryPage(BasePageRequest<AiConversationQueryRequestDTO> request);

    PageResponse<AiConversationResponseDTO> queryGroups(BasePageRequest<AiConversationQueryRequestDTO> request);

    BaseResponse<List<AiConversationResponseDTO>> recoverByMemoryKey(String memoryKey);
}
