package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.model.AiModelCreateRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelQueryRequestDTO;
import org.astrsomn.core.common.dto.model.AiModelResponseDTO;
import org.astrsomn.core.common.dto.model.AiModelUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.common.entity.AiModelEntity;

import org.springframework.beans.BeanUtils;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.server.service.AiModelService;
import org.astrsomn.server.service.support.BizResourceKeyGenerator;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {

    private final BizResourceKeyGenerator bizResourceKeyGenerator;
    private final AstrsomnProperties astrsomnProperties;
    private final AiInstanceMapper aiInstanceMapper;


    @Override
    public BaseResponse<String> delete(long[] longIds) {
        boolean result = removeByIds(Arrays.stream(longIds).boxed().toList());
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request) {
        IPage<AiModelEntity> page = request.buildPage();

        LambdaQueryWrapper<AiModelEntity> wrapper = new LambdaQueryWrapper<>();
        /*
         * 当前请求生效环境（X-Astrsomn-Env-Code + 角色策略 → EnvScope，见 EnvCodeRequestInterceptor）。
         * 与 {@link org.astrsomn.starter.config.EnvCodeTenantHandler} 对 AI_MODEL 的租户条件一致；
         * 此处显式写出便于代码审查与排查，避免误以为未按环境过滤。
         */
        String effectiveEnv = StringUtils.trimToNull(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        if (effectiveEnv != null) {
            wrapper.eq(AiModelEntity::getEnvCode, effectiveEnv);
        }
        AiModelQueryRequestDTO param = request.getParam();
        if (param != null) {
            // AiModelQueryRequestDTO 里有 supplier 字段，但表字段在 AiModelEntity 里对应 provider
            if (StringUtils.isNotBlank(param.getSupplier())) {
                wrapper.eq(AiModelEntity::getProvider, param.getSupplier());
            }
            // 其余字段（例如 modelName、modelKey 等）如果前端传了，也可以直接透传过滤
            if (StringUtils.isNotBlank(param.getModelName())) {
                wrapper.like(AiModelEntity::getModelName, param.getModelName());
            }
            if (StringUtils.isNotBlank(param.getModelKey())) {
                wrapper.eq(AiModelEntity::getModelKey, param.getModelKey());
            }

            if (StringUtils.isNotBlank(param.getStatus())) {
                wrapper.eq(AiModelEntity::getStatus, param.getStatus());
            }
        }

        IPage<AiModelEntity> result = page(page, wrapper);

        // 兜底：有些环境下可能出现 count 正常但分页 records 为空
        // 这种情况下用 list(wrapper) 兜底，再按 pageNo/pageSize 截取，避免前端显示空数据
        List<AiModelEntity> records = result.getRecords();
        if ((records == null || records.isEmpty()) && result.getTotal() > 0) {
            records = list(wrapper);
        }

        long pageNo = request.getPageNo() == null || request.getPageNo() < 1 ? 1 : request.getPageNo();
        long pageSize = request.getPageSize() == null || request.getPageSize() < 1 ? 10 : request.getPageSize();
        int fromIndex = (int) ((pageNo - 1) * pageSize);
        int toIndex = (int) Math.min(fromIndex + pageSize, records != null ? records.size() : 0);

        List<AiModelResponseDTO> list = new ArrayList<>();
        if (records != null && fromIndex < toIndex) {
            for (AiModelEntity e : records.subList(fromIndex, toIndex)) {
                AiModelResponseDTO dto = new AiModelResponseDTO();
                BeanUtils.copyProperties(e, dto);
                list.add(dto);
            }
        }

        PageResponse<AiModelResponseDTO> response = new PageResponse<>();
        response.setTotal(result.getTotal());
        response.setPageSize(result.getSize());
        response.setPageNum(result.getCurrent());
        response.setPages(result.getPages());
        response.setList(list);
        response.setHasNext(result.getCurrent() < result.getPages());

        return response;
    }

    @Override
    public BaseResponse<AiModelResponseDTO> detail(Long longId) {
        AiModelEntity entity = getById(longId);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }

        AiModelResponseDTO responseDTO = new AiModelResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        responseDTO.setModelKeyImmutable(isModelKeyReferencedByInstance(entity.getModelKey(), entity.getEnvCode()));
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> updateModel(AiModelUpdateRequestDTO request) {
        if (request.getId() == null) {
            return BaseResponse.fail("ID不能为空", null);
        }
        AiModelEntity existing = getById(request.getId());
        if (existing == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiModelEntity entity = new AiModelEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }
        if (isModelKeyReferencedByInstance(existing.getModelKey(), existing.getEnvCode())) {
            entity.setModelKey(existing.getModelKey());
        } else {
            assignModelKeyIfBlank(entity);
        }
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public BaseResponse<String> create(AiModelCreateRequestDTO request) {
        AiModelEntity entity = new AiModelEntity();
        BeanUtils.copyProperties(request, entity);
        if (StringUtils.isBlank(entity.getEnvCode())) {
            entity.setEnvCode(EnvRuntime.resolveEffectiveEnvCode(astrsomnProperties));
        }
        assignModelKeyIfBlank(entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    private void assignModelKeyIfBlank(AiModelEntity entity) {
        String trimmed = StringUtils.trimToNull(entity.getModelKey());
        if (trimmed != null) {
            entity.setModelKey(trimmed);
            return;
        }
        String user = StringUtils.defaultIfBlank(entity.getCreateUser(), "0");
        entity.setModelKey(
                bizResourceKeyGenerator.generateUniqueModelKey(
                        entity,
                        candidate ->
                                baseMapper.selectCount(
                                        new LambdaQueryWrapper<AiModelEntity>()
                                                .eq(AiModelEntity::getCreateUser, user)
                                                .eq(AiModelEntity::getModelKey, candidate))));
    }

    private boolean isModelKeyReferencedByInstance(String modelKey, String envCode) {
        if (StringUtils.isBlank(modelKey) || StringUtils.isBlank(envCode)) {
            return false;
        }
        return aiInstanceMapper.selectCount(
                        new LambdaQueryWrapper<AiInstanceEntity>()
                                .eq(AiInstanceEntity::getModelKey, modelKey.trim())
                                .eq(AiInstanceEntity::getEnvCode, envCode.trim()))
                > 0;
    }
}
