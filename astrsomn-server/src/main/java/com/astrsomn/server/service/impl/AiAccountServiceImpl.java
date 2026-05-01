package com.astrsomn.server.service.impl;

import com.astrsomn.core.common.utils.PageConverter;
import com.astrsomn.core.common.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.account.AiAccountCreateRequestDTO;
import com.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import com.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import com.astrsomn.core.common.dto.account.AiAccountUsageStatsDTO;
import com.astrsomn.core.common.dto.account.AiAccountUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiAccountEntity;
import com.astrsomn.core.common.entity.AiModelEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.utils.CryptoUtil;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiAccountErrorEnum;
import com.astrsomn.starter.mapper.AiAccountMapper;
import com.astrsomn.starter.mapper.AiChatMessageMapper;
import com.astrsomn.starter.mapper.AiModelMapper;
import com.astrsomn.server.service.AiAccountService;

import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAccountServiceImpl extends ServiceImpl<AiAccountMapper, AiAccountEntity> implements AiAccountService {


    private final AiModelMapper aiModelMapper;
    private final AiChatMessageMapper aiChatMessageMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;

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
        if (entity.getApiKey() != null) {
            dto.setApiKey(CryptoUtil.decrypt(entity.getApiKey()));
        }
        if (entity.getApiSecret() != null) {
            dto.setApiSecret(CryptoUtil.decrypt(entity.getApiSecret()));
        }
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

    /**
     * 获取解密后的AI账号信息（内部使用）
     */
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

    /**
     * 获取解密后的AI账号信息（内部使用）
     */
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

    /**
     * 同环境下是否存在 AI_MODEL 引用该 accountKey。
     */
    private boolean isAccountKeyReferencedByModel(String accountKey, String envCode) {
        if (StringUtils.isBlank(accountKey) || StringUtils.isBlank(envCode)) {
            return false;
        }
        return aiModelMapper.selectCount(
                        new LambdaQueryWrapper<AiModelEntity>()
                                .eq(AiModelEntity::getAccountKey, accountKey.trim())
                                .eq(AiModelEntity::getEnvCode, envCode.trim()))
                > 0;
    }
}
