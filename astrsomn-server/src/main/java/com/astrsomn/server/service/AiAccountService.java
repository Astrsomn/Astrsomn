package com.astrsomn.server.service;

import com.astrsomn.api.runtime.common.dto.account.AiAccountCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.api.runtime.common.dto.account.AiAccountUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AiAccountService extends IService<AiAccountEntity> {

    BaseResponse<String> create(AiAccountCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiAccountResponseDTO> detail(Long id);

    BaseResponse<String> update(AiAccountUpdateRequestDTO request);

    PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request);

}
