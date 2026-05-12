package com.astrsomn.server.service.impl;

import com.astrsomn.server.mapper.AiVecSourceMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.api.runtime.common.constant.AiVecDriverEnum;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceCreateRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceResponseDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceSetStatusRequestDTO;
import com.astrsomn.api.runtime.common.dto.vecsource.AiVecSourceUpdateRequestDTO;
import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.api.runtime.exception.AstVecSourceErrorEnum;
import com.astrsomn.server.service.AiVecSourceService;
import com.astrsomn.server.service.support.QueryEnvParamHelper;
import com.astrsomn.starter.runtime.langchain.vector.AstroVecSourceFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.runtime.common.utils.PageConverter;
@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecSourceServiceImpl extends ServiceImpl<AiVecSourceMapper, AiVecSourceEntity> implements AiVecSourceService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    private final AstroVecSourceFactory astroVecSourceFactory;

    @Override
    public BaseResponse<String> create(AiVecSourceCreateRequestDTO request) {
        AiVecSourceEntity entity = new AiVecSourceEntity();
        BeanUtils.copyProperties(request, entity);
        queryEnvParamHelper.stampEffectiveEnv(entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED);
        }
        AiVecSourceEntity persisted = getById(entity.getId());
        if (persisted == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED);
        }
        try {
            astroVecSourceFactory.registerOrRefresh(persisted);
        } catch (Exception e) {
            log.warn("向量源启用注册失败，回滚创建记录 id={}: {}", entity.getId(), e.getMessage());
            removeById(entity.getId());
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED, e.getMessage());
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        for (long id : ids) {
            astroVecSourceFactory.removeActiveSource(id);
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiVecSourceUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        AiVecSourceEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        AiVecSourceEntity entity = new AiVecSourceEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED);
        }
        AiVecSourceEntity persisted = getById(request.getId());
        if (persisted == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        try {
            astroVecSourceFactory.registerOrRefresh(persisted);
        } catch (Exception e) {
            log.error("向量源运行时注册失败 id={}: {}", request.getId(), e.getMessage());
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED, e.getMessage());
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecSourceResponseDTO> queryPage(BasePageRequest<AiVecSourceQueryRequestDTO> request) {
        IPage<AiVecSourceResponseDTO> page = PageUtils.buildPage(request);
        AiVecSourceQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecSourceQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecSourceResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecSourceResponseDTO> detail(Long id) {
        AiVecSourceEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        AiVecSourceResponseDTO responseDTO = new AiVecSourceResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> setEnabledStatus(AiVecSourceSetStatusRequestDTO request) {
        if (request == null || request.getId() == null || request.getEnabled() == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        AiVecSourceEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        String newStatus =
                Boolean.TRUE.equals(request.getEnabled())
                        ? AiVecDriverEnum.StatusEnum.ENABLED.getCode()
                        : AiVecDriverEnum.StatusEnum.DISABLED.getCode();
        LambdaUpdateWrapper<AiVecSourceEntity> uw = new LambdaUpdateWrapper<>();
        uw.eq(AiVecSourceEntity::getId, request.getId()).set(AiVecSourceEntity::getStatus, newStatus);
        boolean updated = update(uw);
        if (!updated) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED);
        }
        AiVecSourceEntity persisted = getById(request.getId());
        if (persisted == null) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(request.getEnabled())) {
            try {
                astroVecSourceFactory.registerOrRefresh(persisted);
            } catch (Exception e) {
                LambdaUpdateWrapper<AiVecSourceEntity> revert = new LambdaUpdateWrapper<>();
                revert.eq(AiVecSourceEntity::getId, request.getId())
                        .set(AiVecSourceEntity::getStatus, AiVecDriverEnum.StatusEnum.DISABLED.getCode());
                update(revert);
                astroVecSourceFactory.removeActiveSource(request.getId());
                log.warn("向量源启用失败，已回滚为禁用 id={}: {}", request.getId(), e.getMessage());
                throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED, e.getMessage());
            }
            return BaseResponse.success("已启用并加载连接");
        }
        astroVecSourceFactory.removeActiveSource(request.getId());
        return BaseResponse.success("已禁用并释放连接");
    }

    @Override
    public BaseResponse<String> testConnection(AiVecSourceCreateRequestDTO request) {
        long t0 = System.nanoTime();
        try {
            AiVecSourceEntity entity = new AiVecSourceEntity();
            BeanUtils.copyProperties(request, entity);
            log.info(
                    "[AiVecSource] testConnection start: provider={}, host={}, port={}, envCode={}, sourceId={}, tokenConfigured={}",
                    entity.getExtensionCode(),
                    entity.getHost(),
                    entity.getPort(),
                    entity.getEnvCode(),
                    entity.getId(),
                    StringUtils.isNotBlank(entity.getToken()));
            boolean success = astroVecSourceFactory.testConnection(entity);
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            if (success) {
                log.info(
                        "[AiVecSource] testConnection success in {}ms: provider={}, host={}, port={}",
                        elapsedMs,
                        entity.getExtensionCode(),
                        entity.getHost(),
                        entity.getPort());
                return BaseResponse.success("连接测试成功");
            }
            log.warn(
                    "[AiVecSource] testConnection failed in {}ms: provider={}, host={}, port={}",
                    elapsedMs,
                    entity.getExtensionCode(),
                    entity.getHost(),
                    entity.getPort());
            return BaseResponse.fail("连接测试失败");
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error("[AiVecSource] testConnection error in {}ms: {}", elapsedMs, e.getMessage(), e);
            return BaseResponse.fail("连接测试失败: " + e.getMessage());
        }
    }
}
