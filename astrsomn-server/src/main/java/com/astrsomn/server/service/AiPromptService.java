package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiPromptEntity;

import java.util.List;

public interface AiPromptService extends IService<AiPromptEntity> {
    BaseResponse<String> create(AiPromptCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiPromptResponseDTO> detail(Long id);

    BaseResponse<String> update(AiPromptUpdateRequestDTO request);

    PageResponse<AiPromptResponseDTO> queryPage(BasePageRequest<AiPromptQueryRequestDTO> request);

    /**
     * 按 promptKey + env 查询全部历史版本（版本号倒序）。
     */
    BaseResponse<List<AiPromptResponseDTO>> history(String promptKey, String envCode);
}
