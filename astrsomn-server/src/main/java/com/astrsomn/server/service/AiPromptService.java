package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;

import java.util.List;

public interface AiPromptService extends IService<AiPromptEntity> {
    BaseResponse<String> create(AiPromptCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiPromptResponseDTO> detail(Long id);

    BaseResponse<String> update(AiPromptUpdateRequestDTO request);

    PageResponse<AiPromptResponseDTO> queryPage(BasePageRequest<AiPromptQueryRequestDTO> request);

    BaseResponse<List<AiPromptResponseDTO>> history(String promptKey, String envCode);

    BaseResponse<String> improvePrompt(AiPromptUpdateRequestDTO request);

    BaseResponse<List<String>> querySceneTags();
}
