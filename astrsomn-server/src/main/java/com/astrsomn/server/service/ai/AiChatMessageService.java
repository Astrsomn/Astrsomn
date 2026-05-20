package com.astrsomn.server.service.ai;

import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageResponseDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.AiChatMessageUpdateRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.message.restore.AiChatTurnBundleDTO;
import com.astrsomn.api.runtime.common.entity.AiChatMessageEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AiChatMessageService extends IService<AiChatMessageEntity> {
    BaseResponse<String> create(AiChatMessageCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiChatMessageResponseDTO> detail(Long id);

    BaseResponse<String> update(AiChatMessageUpdateRequestDTO request);

    PageResponse<AiChatMessageResponseDTO> queryPage(BasePageRequest<AiChatMessageQueryRequestDTO> request);


    BaseResponse<List<AiChatMessageResponseDTO>> recoverByMemoryKey(String memoryKey);

    
    BaseResponse<List<AiChatTurnBundleDTO>> recoverTurnsByMemoryKey(String memoryKey);
}
