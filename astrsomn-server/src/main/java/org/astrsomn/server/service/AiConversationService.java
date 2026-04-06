package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.conversation.AiConversationCreateRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiConversationEntity;

public interface AiConversationService extends IService<AiConversationEntity> {
    BaseResponse<String> create(AiConversationCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiConversationResponseDTO> detail(Long id);

    BaseResponse<String> update(AiConversationUpdateRequestDTO request);

    PageResponse<AiConversationResponseDTO> queryPage(BasePageRequest<AiConversationQueryRequestDTO> request);
}
