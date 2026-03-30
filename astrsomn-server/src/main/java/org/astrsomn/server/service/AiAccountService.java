package org.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.account.AiAccountCreateRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import org.astrsomn.core.common.dto.account.AiAccountUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiAccountEntity;

public interface AiAccountService extends IService<AiAccountEntity> {

    BaseResponse<String> create(AiAccountCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiAccountResponseDTO> detail(Long id);

    BaseResponse<String> update(AiAccountUpdateRequestDTO request);

    PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request);
}
