package com.astrsomn.server.service;

import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionCreateRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.core.common.dto.chat.session.AiChatSessionUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiChatSessionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiChatSessionService extends IService<AiChatSessionEntity> {

    BaseResponse<String> create(AiChatSessionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiChatSessionUpdateRequestDTO request);

    PageResponse<AiChatSessionResponseDTO> queryPage(BasePageRequest<AiChatSessionQueryRequestDTO> request);

    BaseResponse<AiChatSessionResponseDTO> detail(Long id);
}
