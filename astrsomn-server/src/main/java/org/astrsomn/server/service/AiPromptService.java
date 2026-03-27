package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.prompt.AiPromptCreateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptUpdateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import org.astrsomn.core.common.entity.AiPromptEntity;

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
