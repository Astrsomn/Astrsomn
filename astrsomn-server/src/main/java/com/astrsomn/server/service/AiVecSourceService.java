package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceResponseDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceSetStatusRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;

public interface AiVecSourceService extends IService<AiVecSourceEntity> {

    BaseResponse<String> create(AiVecSourceCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<String> update(AiVecSourceUpdateRequestDTO request);

    BaseResponse<String> testConnection(AiVecSourceCreateRequestDTO request);

    BaseResponse<String> setEnabledStatus(AiVecSourceSetStatusRequestDTO request);

    PageResponse<AiVecSourceResponseDTO> queryPage(BasePageRequest<AiVecSourceQueryRequestDTO> request);

    BaseResponse<AiVecSourceResponseDTO> detail(Long id);
}
