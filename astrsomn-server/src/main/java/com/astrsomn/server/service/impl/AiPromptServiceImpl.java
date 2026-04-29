package com.astrsomn.server.service.impl;
import com.astrsomn.core.common.utils.PageConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import com.astrsomn.commn.base.BasePageRequest;
import com.astrsomn.commn.base.BaseResponse;
import com.astrsomn.commn.base.PageResponse;
import com.astrsomn.core.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.core.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.core.common.entity.AiPromptEntity;
import com.astrsomn.commn.utils.StringUtils;
import com.astrsomn.commn.base.BusinessException;
import com.astrsomn.core.exception.AiPromptErrorEnum;
import com.astrsomn.starter.mapper.AiPromptMapper;
import com.astrsomn.server.service.AiPromptService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.astrsomn.core.common.utils.PageUtils;
@Service
@RequiredArgsConstructor
public class AiPromptServiceImpl extends ServiceImpl<AiPromptMapper, AiPromptEntity> implements AiPromptService {


    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiPromptCreateRequestDTO request) {
        AiPromptEntity entity = new AiPromptEntity();
        BeanUtils.copyProperties(request, entity);

        String env = StringUtils.defaultIfBlank(entity.getEnvCode(), queryEnvParamHelper.effectiveEnvCode());
        entity.setEnvCode(env);
        if (entity.getVersion() == null) {
            entity.setVersion(1);
        }
        long exists = lambdaQuery()
                .eq(AiPromptEntity::getPromptKey, entity.getPromptKey())
                .eq(AiPromptEntity::getEnvCode, env)
                .count();
        if (exists > 0) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
        }
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
        }
        Set<String> seen = new LinkedHashSet<>();
        for (long id : ids) {
            AiPromptEntity row = getById(id);
            if (row == null) {
                continue;
            }
            String pair = row.getEnvCode() + "\0" + row.getPromptKey();
            if (seen.add(pair)) {
                remove(new LambdaQueryWrapper<AiPromptEntity>()
                        .eq(AiPromptEntity::getEnvCode, row.getEnvCode())
                        .eq(AiPromptEntity::getPromptKey, row.getPromptKey()));
            }
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiPromptResponseDTO> detail(Long id) {
        AiPromptEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_NOT_FOUND);
        }
        AiPromptResponseDTO responseDTO = new AiPromptResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiPromptUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
        }
        AiPromptEntity current = getById(request.getId());
        if (current == null) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_NOT_FOUND);
        }
        AiPromptEntity next = new AiPromptEntity();
        BeanUtils.copyProperties(request, next);
        next.setId(null);
        next.setCreateTime(null);
        next.setUpdateTime(null);
        String env = StringUtils.defaultIfBlank(current.getEnvCode(), queryEnvParamHelper.effectiveEnvCode());
        next.setEnvCode(env);
        String requestedPromptKey = StringUtils.trimToNull(request.getPromptKey());
        String nextPromptKey = StringUtils.defaultIfBlank(requestedPromptKey, current.getPromptKey());
        next.setPromptKey(nextPromptKey);
        if (!StringUtils.equals(nextPromptKey, current.getPromptKey())) {
            long exists = lambdaQuery()
                    .eq(AiPromptEntity::getPromptKey, nextPromptKey)
                    .eq(AiPromptEntity::getEnvCode, env)
                    .count();
            if (exists > 0) {
                throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
            }
        }
        AiPromptEntity top = lambdaQuery()
                .eq(AiPromptEntity::getPromptKey, nextPromptKey)
                .eq(AiPromptEntity::getEnvCode, env)
                .orderByDesc(AiPromptEntity::getVersion)
                .last("LIMIT 1")
                .one();
        int base = 0;
        if (top != null && top.getVersion() != null) {
            base = top.getVersion();
        }
        next.setVersion(base + 1);
        boolean result = save(next);
        if (!result) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_UPDATE_FAILED);
        }
        return BaseResponse.success("已保存为新版本");
    }

    @Override
    public PageResponse<AiPromptResponseDTO> queryPage(BasePageRequest<AiPromptQueryRequestDTO> request) {
        IPage<AiPromptResponseDTO> page = PageUtils.buildPage(request);
        AiPromptQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiPromptQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiPromptResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<List<AiPromptResponseDTO>> history(String promptKey, String envCode) {
        if (StringUtils.isBlank(promptKey)) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
        }
        if (StringUtils.isBlank(envCode)) {
            envCode = queryEnvParamHelper.effectiveEnvCode();
        }
        List<AiPromptResponseDTO> list = baseMapper.listHistoryByPromptKey(promptKey.trim(), envCode);
        return BaseResponse.success(list);
    }
}
