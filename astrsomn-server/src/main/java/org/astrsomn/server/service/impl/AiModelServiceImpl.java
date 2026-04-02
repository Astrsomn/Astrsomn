package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.util.StringUtils;
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
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.astrsomn.starter.config.AstrsomnProperties;
import org.astrsomn.starter.context.EnvRuntime;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelEntity> implements AiModelService {

    private final BizResourceKeyGenerator bizResourceKeyGenerator;
    private final AstrsomnProperties astrsomnProperties;
    private final AiInstanceMapper aiInstanceMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;


    @Override
    public BaseResponse<String> delete(long[] longIds) {
        boolean result = removeByIds(Arrays.stream(longIds).boxed().toList());
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public PageResponse<AiModelResponseDTO> queryPage(BasePageRequest<AiModelQueryRequestDTO> request) {
        IPage<AiModelResponseDTO> page = request.buildPage();
        AiModelQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiModelQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiModelResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
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
