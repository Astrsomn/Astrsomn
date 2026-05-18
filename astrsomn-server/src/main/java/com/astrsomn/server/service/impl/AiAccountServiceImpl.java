package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.dto.account.*;
import com.astrsomn.api.runtime.common.entity.AiAccountEntity;
import com.astrsomn.api.runtime.common.entity.AiInstanceEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiAccountErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiAccountMapper;
import com.astrsomn.server.mapper.AiChatMessageMapper;
import com.astrsomn.server.mapper.AiInstanceMapper;
import com.astrsomn.server.service.AiAccountService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAccountServiceImpl extends ServiceImpl<AiAccountMapper, AiAccountEntity> implements AiAccountService {


    private final AiInstanceMapper aiInstanceMapper;
    private final AiChatMessageMapper aiChatMessageMapper;

    @Override
    public BaseResponse<String> create(AiAccountCreateRequestDTO request) {
        AiAccountEntity entity = new AiAccountEntity();
        BeanUtils.copyProperties(request, entity);
        // 加密API Key和Secret
        if (request.getApiKey() != null) {
            entity.setApiKey(CryptoUtil.encrypt(request.getApiKey()));
        }
        if (request.getApiSecret() != null) {
            entity.setApiSecret(CryptoUtil.encrypt(request.getApiSecret()));
        }

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiAccountResponseDTO> detail(Long id) {
        AiAccountEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_NOT_FOUND);
        }
        AiAccountResponseDTO dto = new AiAccountResponseDTO();
        BeanUtils.copyProperties(entity, dto);
//        if (entity.getApiKey() != null) {
//            dto.setApiKey(CryptoUtil.decrypt(entity.getApiKey()));
//        }
//        if (entity.getApiSecret() != null) {
//            dto.setApiSecret(CryptoUtil.decrypt(entity.getApiSecret()));
//        }
        dto.setAccountKeyImmutable(isAccountKeyReferencedByModel(entity.getAccountKey(), entity.getEnvCode()));
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> update(AiAccountUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_PARAM_ERROR);
        }
        AiAccountEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_NOT_FOUND);
        }
        AiAccountEntity entity = new AiAccountEntity();
        BeanUtils.copyProperties(request, entity);
        // 加密API Key和Secret
        if (request.getApiKey() != null) {
            entity.setApiKey(CryptoUtil.encrypt(request.getApiKey()));
        }
        if (request.getApiSecret() != null) {
            entity.setApiSecret(CryptoUtil.encrypt(request.getApiSecret()));
        }
        if (isAccountKeyReferencedByModel(existing.getAccountKey(), existing.getEnvCode())) {
            entity.setAccountKey(existing.getAccountKey());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getExtensionCode())) {
            entity.setExtensionCode(request.getExtensionCode());
        }

        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request) {
        IPage<AiAccountResponseDTO> page = PageUtils.buildPage(request);
        AiAccountQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiAccountQueryRequestDTO();
        }

        IPage<AiAccountResponseDTO> result = baseMapper.queryPage(page, param);
        fillAccountUsageStats(result.getRecords(), param.getEnvCode());
        return PageConverter.toResponse(result);
    }

    private void fillAccountUsageStats(List<AiAccountResponseDTO> records, String envCode) {
        if (records == null || records.isEmpty()) {
            return;
        }

        List<String> accountKeys = records.stream()
                .map(AiAccountResponseDTO::getAccountKey)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        if (accountKeys.isEmpty()) {
            return;
        }

        List<AiAccountUsageStatsDTO> statsList = aiChatMessageMapper.selectUsageByAccountKeys(envCode, accountKeys);
        Map<String, AiAccountUsageStatsDTO> statsMap = (statsList == null ? Collections.<AiAccountUsageStatsDTO>emptyList() : statsList)
                .stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.isNotBlank(item.getAccountKey()))
                .collect(Collectors.toMap(AiAccountUsageStatsDTO::getAccountKey, Function.identity(), (left, right) -> left));

        for (AiAccountResponseDTO dto : records) {
            if (dto == null || StringUtils.isBlank(dto.getAccountKey())) {
                continue;
            }
            AiAccountUsageStatsDTO stats = statsMap.get(dto.getAccountKey());
            if (stats == null) {
                dto.setCallCount(0L);
                dto.setPromptTokens(0L);
                dto.setCompletionTokens(0L);
                dto.setTotalTokens(0L);
                continue;
            }
            dto.setCallCount(stats.getCallCount() == null ? 0L : stats.getCallCount());
            dto.setPromptTokens(stats.getPromptTokens() == null ? 0L : stats.getPromptTokens());
            dto.setCompletionTokens(stats.getCompletionTokens() == null ? 0L : stats.getCompletionTokens());
            dto.setTotalTokens(stats.getTotalTokens() == null ? 0L : stats.getTotalTokens());
        }
    }

    
    public AiAccountEntity getDecryptedAccount(Long id) {
        AiAccountEntity entity = getById(id);
        if (entity != null) {
            if (entity.getApiKey() != null) {
                entity.setApiKey(CryptoUtil.decrypt(entity.getApiKey()));
            }
            if (entity.getApiSecret() != null) {
                entity.setApiSecret(CryptoUtil.decrypt(entity.getApiSecret()));
            }
        }
        return entity;
    }

    
    public AiAccountEntity getDecryptedAccountByKey(String accountKey, String envCode) {
        AiAccountEntity entity = getOne(new LambdaQueryWrapper<AiAccountEntity>()
                .eq(AiAccountEntity::getAccountKey, accountKey)
                .eq(AiAccountEntity::getEnvCode, envCode));
        if (entity != null) {
            if (entity.getApiKey() != null) {
                entity.setApiKey(CryptoUtil.decrypt(entity.getApiKey()));
            }
            if (entity.getApiSecret() != null) {
                entity.setApiSecret(CryptoUtil.decrypt(entity.getApiSecret()));
            }
        }
        return entity;
    }

    
    private boolean isAccountKeyReferencedByModel(String accountKey, String envCode) {
        if (StringUtils.isBlank(accountKey) || StringUtils.isBlank(envCode)) {
            return false;
        }
        return aiInstanceMapper.selectCount(
                new LambdaQueryWrapper<AiInstanceEntity>()
                        .eq(AiInstanceEntity::getAccountKey, accountKey.trim())
                        .eq(AiInstanceEntity::getEnvCode, envCode.trim()))
                > 0;
    }
}
