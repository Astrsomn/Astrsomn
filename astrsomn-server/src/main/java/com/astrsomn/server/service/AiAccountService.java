package com.astrsomn.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.astrsomn.core.common.base.BasePageRequest;
import com.astrsomn.core.common.base.BaseResponse;
import com.astrsomn.core.common.base.PageResponse;
import com.astrsomn.core.common.dto.account.AiAccountCreateRequestDTO;
import com.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.core.common.dto.account.AiAccountUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiAccountEntity;

public interface AiAccountService extends IService<AiAccountEntity> {

    BaseResponse<String> create(AiAccountCreateRequestDTO request);

    BaseResponse<String> delete(long[] ids);

    BaseResponse<AiAccountResponseDTO> detail(Long id);

    BaseResponse<String> update(AiAccountUpdateRequestDTO request);

    PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request);

    /**
     * 获取解密后的AI账号信息（内部使用）
     */
    AiAccountEntity getDecryptedAccount(Long id);

    /**
     * 获取解密后的AI账号信息（内部使用）
     */
    AiAccountEntity getDecryptedAccountByKey(String accountKey, String envCode);
}
