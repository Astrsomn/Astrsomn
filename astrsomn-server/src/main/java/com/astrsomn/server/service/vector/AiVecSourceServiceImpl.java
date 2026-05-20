package com.astrsomn.server.service.vector;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.dto.vecsource.*;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.exception.AstVecSourceErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecSourceMapper;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecSourceServiceImpl extends ServiceImpl<AiVecSourceMapper, AiVecSourceEntity> implements AiVecSourceService {

    private final AstroVecSourceFactory astroVecSourceFactory;

    @Override
    public BaseResponse<String> create(AiVecSourceCreateRequestDTO request) {
        AiVecSourceEntity entity = new AiVecSourceEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED);
        }
        AiVecSourceEntity persisted = getById(entity.getId());
        if (Objects.isNull(persisted)) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED);
        }
        try {
            astroVecSourceFactory.registerOrRefresh(persisted);
        } catch (Exception e) {
            log.warn("Vector source registration failed, rolling back created record id={}: {}", entity.getId(), e.getMessage());
            removeById(entity.getId());
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED, e.getMessage());
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (Objects.isNull(ids) || ids.length == 0) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        for (long id : ids) {
            astroVecSourceFactory.removeActiveSource(id);
            removeById(id);
        }
        return BaseResponse.success("success");
    }

    @Override
    public BaseResponse<String> update(AiVecSourceUpdateRequestDTO request) {
        if (Objects.isNull(request.getId())) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        AiVecSourceEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        AiVecSourceEntity entity = new AiVecSourceEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED);
        }
        AiVecSourceEntity persisted = getById(request.getId());
        if (Objects.isNull(persisted)) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        try {
            astroVecSourceFactory.registerOrRefresh(persisted);
        } catch (Exception e) {
            log.error("Vector source runtime registration failed id={}: {}", request.getId(), e.getMessage());
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED, e.getMessage());
        }
        return BaseResponse.success("success");
    }

    @Override
    public PageResponse<AiVecSourceResponseDTO> queryPage(BasePageRequest<AiVecSourceQueryRequestDTO> request) {
        IPage<AiVecSourceResponseDTO> page = PageUtils.buildPage(request);
        AiVecSourceQueryRequestDTO param = request.getParam();
        if (Objects.isNull(param)) {
            param = new AiVecSourceQueryRequestDTO();
        }
        IPage<AiVecSourceResponseDTO> result = baseMapper.queryPage(page, param);
        return PageConverter.toResponse(result);
    }

    @Override
    public BaseResponse<AiVecSourceResponseDTO> detail(Long id) {
        AiVecSourceEntity entity = getById(id);
        if (Objects.isNull(entity)) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_NOT_FOUND);
        }
        AiVecSourceResponseDTO responseDTO = new AiVecSourceResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> setEnabledStatus(AiVecSourceSetStatusRequestDTO request) {
        if (Objects.isNull(request) || Objects.isNull(request.getId()) || Objects.isNull(request.getEnabled())) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        AiVecSourceEntity existing = getById(request.getId());
        if (Objects.isNull(existing)) {
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
        if (Objects.isNull(persisted)) {
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
                log.warn("Vector source enable failed, rolled back to disabled id={}: {}", request.getId(), e.getMessage());
                throw new BusinessException(AstVecSourceErrorEnum.SOURCE_UPDATE_FAILED, e.getMessage());
            }
            return BaseResponse.success("Enabled and connected");
        }
        astroVecSourceFactory.removeActiveSource(request.getId());
        return BaseResponse.success("Disabled and disconnected");
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
                return BaseResponse.success("Connection test succeeded");
            }
            log.warn(
                    "[AiVecSource] testConnection failed in {}ms: provider={}, host={}, port={}",
                    elapsedMs,
                    entity.getExtensionCode(),
                    entity.getHost(),
                    entity.getPort());
            return BaseResponse.fail("Connection test failed");
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - t0) / 1_000_000L;
            log.error("[AiVecSource] testConnection error in {}ms: {}", elapsedMs, e.getMessage(), e);
            return BaseResponse.fail("Connection test failed: " + e.getMessage());
        }
    }
}
