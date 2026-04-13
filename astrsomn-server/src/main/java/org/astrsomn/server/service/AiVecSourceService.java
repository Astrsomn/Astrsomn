package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceCreateRequestDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceResponseDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceSetStatusRequestDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecSourceEntity;

public interface AiVecSourceService extends IService<AiVecSourceEntity> {

    BaseResponse<String> create(AiVecSourceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecSourceUpdateRequestDTO request);

    BaseResponse<String> testConnection(AiVecSourceCreateRequestDTO request);

    BaseResponse<String> setEnabledStatus(AiVecSourceSetStatusRequestDTO request);

    PageResponse<AiVecSourceResponseDTO> queryPage(BasePageRequest<AiVecSourceQueryRequestDTO> request);

    BaseResponse<AiVecSourceResponseDTO> detail(Long id);
}
