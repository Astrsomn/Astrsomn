package com.astrsomn.server.service.impl;

import com.astrsomn.api.runtime.common.dto.prompt.AiPromptCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptResponseDTO;
import com.astrsomn.api.runtime.common.dto.prompt.AiPromptUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiPromptEntity;
import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.exception.AiPromptErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.JsonUtil;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.astrsomn.assistant.PromptAssistant;
import com.astrsomn.server.mapper.AiPromptMapper;
import com.astrsomn.server.service.AiPromptService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.runtime.langchain.aop.annotation.Astro;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiPromptServiceImpl extends ServiceImpl<AiPromptMapper, AiPromptEntity> implements AiPromptService {


    private final QueryEnvParamHelper queryEnvParamHelper;

    @Astro(bizKey = "AG-ASTRSOMN-PROMPT", envCode = "PRO")
    private PromptAssistant promptAssistant;

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

    @Override
    public BaseResponse<AiPromptResponseDTO> submit(AiPromptUpdateRequestDTO request) {
        String rawContent = StringUtils.trimToNull(request.getPromptContent());
        if (rawContent == null) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
        }

        String env = queryEnvParamHelper.effectiveEnvCode();
        String promptKey = StringUtils.trimToNull(request.getPromptKey());

        AiPromptEntity entity = new AiPromptEntity();
        entity.setPromptContent(request.getPromptContent());
        entity.setPromptTitle(request.getPromptTitle());
        entity.setScene(request.getScene());
        entity.setEnvCode(env);

        if (promptKey == null) {
            // 无 promptKey → 新建
            entity.setPromptKey("PT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
            entity.setVersion(1);
        } else {
            // 有 promptKey → 追加新版本
            entity.setPromptKey(promptKey);
            AiPromptEntity top = lambdaQuery()
                    .eq(AiPromptEntity::getPromptKey, promptKey)
                    .eq(AiPromptEntity::getEnvCode, env)
                    .orderByDesc(AiPromptEntity::getVersion)
                    .last("LIMIT 1")
                    .one();
            int base = (top != null && top.getVersion() != null) ? top.getVersion() : 0;
            entity.setVersion(base + 1);
        }

        if (!save(entity)) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_CREATE_FAILED);
        }

        AiPromptResponseDTO dto = new AiPromptResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> beautify(String promptContent) {
        String rawContent = StringUtils.trimToNull(promptContent);
        if (rawContent == null) {
            throw new BusinessException(AiPromptErrorEnum.PROMPT_PARAM_ERROR);
        }
        String result = promptAssistant.submit(rawContent, UUID.randomUUID().toString());
        return BaseResponse.success(result);
    }

    @Override
    public BaseResponse<List<String>> querySceneTags() {
        String envCode = queryEnvParamHelper.effectiveEnvCode();
        List<String> rawScenes = baseMapper.querySceneRawList(envCode);
        if (rawScenes == null || rawScenes.isEmpty()) {
            return BaseResponse.success(new ArrayList<>());
        }
        Set<String> tags = new LinkedHashSet<>();
        for (String rawScene : rawScenes) {
            if (StringUtils.isBlank(rawScene)) {
                continue;
            }
            try {
                List<String> parsed = JsonUtil.parseArray(rawScene, String.class);
                if (parsed == null || parsed.isEmpty()) {
                    continue;
                }
                for (String tag : parsed) {
                    String normalized = StringUtils.trimToNull(tag);
                    if (normalized != null) {
                        tags.add(normalized);
                    }
                }
            } catch (RuntimeException ignore) {
            }
        }
        return BaseResponse.success(new ArrayList<>(tags));
    }
}
