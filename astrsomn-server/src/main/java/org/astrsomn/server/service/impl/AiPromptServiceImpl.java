package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.prompt.AiPromptCreateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptUpdateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import org.astrsomn.core.common.entity.AiPromptEntity;
import org.astrsomn.core.mapper.AiPromptMapper;
import org.astrsomn.server.service.AiPromptService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiPromptServiceImpl extends ServiceImpl<AiPromptMapper, AiPromptEntity> implements AiPromptService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiPromptCreateRequestDTO request) {
        AiPromptEntity entity = new AiPromptEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignPromptKeyIfBlank(entity);
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
            return BaseResponse.fail("该 Prompt Key 已存在，请从列表进入编辑或留空由系统生成", null);
        }
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            return BaseResponse.fail("请选择要删除的记录", null);
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
            return BaseResponse.fail("记录不存在", null);
        }
        AiPromptResponseDTO responseDTO = new AiPromptResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiPromptUpdateRequestDTO request) {
        if (request.getId() == null) {
            return BaseResponse.fail("缺少记录 ID", null);
        }
        AiPromptEntity current = getById(request.getId());
        if (current == null) {
            return BaseResponse.fail("记录不存在", null);
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
                return BaseResponse.fail("该 Prompt Key 已存在，请使用其他 Prompt Key", null);
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
        return result ? BaseResponse.success("已保存为新版本") : BaseResponse.fail("保存失败", null);
    }

    @Override
    public PageResponse<AiPromptResponseDTO> queryPage(BasePageRequest<AiPromptQueryRequestDTO> request) {
        IPage<AiPromptResponseDTO> page = request.buildPage();
        AiPromptQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiPromptQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiPromptResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<List<AiPromptResponseDTO>> history(String promptKey, String envCode) {
        if (StringUtils.isBlank(promptKey)) {
            return BaseResponse.fail("缺少 promptKey", null);
        }
        if (StringUtils.isBlank(envCode)) {
            envCode = queryEnvParamHelper.effectiveEnvCode();
        }
        List<AiPromptResponseDTO> list = baseMapper.listHistoryByPromptKey(promptKey.trim(), envCode);
        return BaseResponse.success(list);
    }
}
