package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionResponseDTO;
import com.astrsomn.api.runtime.common.dto.chat.session.AiChatSessionUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiChatSessionEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiChatSessionService extends IService<AiChatSessionEntity> {

    BaseResponse<String> create(AiChatSessionCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiChatSessionUpdateRequestDTO request);

    PageResponse<AiChatSessionResponseDTO> queryPage(BasePageRequest<AiChatSessionQueryRequestDTO> request);

    BaseResponse<AiChatSessionResponseDTO> detail(Long id);
}
