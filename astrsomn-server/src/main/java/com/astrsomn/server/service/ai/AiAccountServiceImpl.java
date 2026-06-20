package com.astrsomn.server.service.ai;

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
import com.astrsomn.common.utils.CollectionUtils;
import com.astrsomn.common.utils.CryptoUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiAccountMapper;
import com.astrsomn.server.mapper.AiChatMessageMapper;
import com.astrsomn.server.mapper.AiInstanceMapper;
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
        if (Objects.nonNull(request.getApiKey())) {
            entity.setApiKey(CryptoUtil.encrypt(request.getApiKey()));
        }
        if (Objects.nonNull(request.getApiSecret())) {
            entity.setApiSecret(CryptoUtil.encrypt(request.getApiSecret()));
        }

        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_CREATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_DELETE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<AiAccountResponseDTO> detail(Long id) {
        AiAccountEntity entity = getById(id);
        if (Objects.isNull(entity)) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_NOT_FOUND);
        }
        AiAccountResponseDTO dto = new AiAccountResponseDTO();
        BeanUtils.copyProperties(entity, dto);

        dto.setAccountKeyImmutable(isAccountKeyReferencedByInstance(entity.getAccountKey(), entity.getEnvCode()));
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> update(AiAccountUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_PARAM_ERROR);
        }
        AiAccountEntity entity = getById(request.getId());
        if (Objects.isNull(entity)) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_NOT_FOUND);
        }

        if (StringUtils.isNotBlank(request.getAccountName())) {
            entity.setAccountName(request.getAccountName());
        }
        if (StringUtils.isNotBlank(request.getApiKey())) {
            entity.setApiKey(CryptoUtil.encrypt(request.getApiKey()));
        }
        if (StringUtils.isNotBlank(request.getApiSecret())) {
            entity.setApiSecret(CryptoUtil.encrypt(request.getApiSecret()));
        }
        if (Objects.nonNull(request.getApiUrl())) {
            entity.setApiUrl(request.getApiUrl());
        }
        if (Objects.nonNull(request.getAccountTokens())) {
            entity.setAccountTokens(request.getAccountTokens());
        }
        if (Objects.nonNull(request.getStatus())) {
            entity.setStatus(request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getExtensionCode())) {
            entity.setExtensionCode(request.getExtensionCode());
        }
        if (StringUtils.isNotBlank(request.getAccountKey())
                && !isAccountKeyReferencedByInstance(entity.getAccountKey(), entity.getEnvCode())) {
            entity.setAccountKey(request.getAccountKey());
        }

        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiAccountErrorEnum.ACCOUNT_UPDATE_FAILED);
        }
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request) {
        IPage<AiAccountResponseDTO> page = PageUtils.buildPage(request);
        AiAccountQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiAccountQueryRequestDTO();
        }

        IPage<AiAccountResponseDTO> result = baseMapper.queryPage(page, param);
        fillAccountUsageStats(result.getRecords());
        return PageConverter.toResponse(result);
    }

    private void fillAccountUsageStats(List<AiAccountResponseDTO> records) {
        if (CollectionUtils.isEmpty(records)) {
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

        List<AiAccountUsageStatsDTO> statsList = aiChatMessageMapper.selectUsageByAccountKeys( accountKeys);
        Map<String, AiAccountUsageStatsDTO> statsMap = Optional.ofNullable(statsList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.isNotBlank(item.getAccountKey()))
                .collect(Collectors.toMap(AiAccountUsageStatsDTO::getAccountKey, Function.identity(), (left, right) -> left));

        records.stream()
                .filter(Objects::nonNull)
                .filter(dto -> StringUtils.isNotBlank(dto.getAccountKey()))
                .forEach(dto -> {
                    AiAccountUsageStatsDTO stats = statsMap.get(dto.getAccountKey());
                    dto.setCallCount(Optional.ofNullable(stats).map(AiAccountUsageStatsDTO::getCallCount).orElse(0L));
                    dto.setPromptTokens(Optional.ofNullable(stats).map(AiAccountUsageStatsDTO::getPromptTokens).orElse(0L));
                    dto.setCompletionTokens(Optional.ofNullable(stats).map(AiAccountUsageStatsDTO::getCompletionTokens).orElse(0L));
                    dto.setTotalTokens(Optional.ofNullable(stats).map(AiAccountUsageStatsDTO::getTotalTokens).orElse(0L));
                });
    }


    private boolean isAccountKeyReferencedByInstance(String accountKey, String envCode) {
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
