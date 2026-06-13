package com.astrsomn.server.service.vector;

import com.astrsomn.api.runtime.common.utils.PageConverter;
import com.astrsomn.api.runtime.common.utils.PageUtils;
import com.astrsomn.api.vector.constant.AiVecDriverEnum;
import com.astrsomn.api.vector.dto.vecsource.*;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.api.vector.entity.AiVecStoreEntity;
import com.astrsomn.api.vector.exception.AstVecSourceErrorEnum;
import com.astrsomn.common.base.BasePageRequest;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.base.BusinessException;
import com.astrsomn.common.base.PageResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.server.mapper.AiVecSourceMapper;
import com.astrsomn.server.mapper.AiVecStoreMapper;
import com.astrsomn.starter.runtime.vector.AstroVecSourceFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.astrsomn.api.runtime.common.langchain.extension.vector.VecSource;
import com.astrsomn.api.vector.exception.AstVecStoreErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiVecSourceServiceImpl extends ServiceImpl<AiVecSourceMapper, AiVecSourceEntity> implements AiVecSourceService {

    private final AstroVecSourceFactory astroVecSourceFactory;
    private final AiVecStoreMapper aiVecStoreMapper;

    @Override
    public BaseResponse<String> create(AiVecSourceCreateRequestDTO request) {
        AiVecSourceEntity entity = new AiVecSourceEntity();
        BeanUtils.copyProperties(request, entity);
        if (Objects.isNull(entity.getAutoConnect())) {
            entity.setAutoConnect(1);
        }
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
            // 先级联删除关联的数据库（AiVecStore）
            deleteAssociatedStores(id);
            
            // 移除活跃的数据源连接
            astroVecSourceFactory.removeActiveSource(id);
            
            // 删除数据源本身
            removeById(id);
        }
        return BaseResponse.success("success");
    }
    
    /**
     * 级联删除数据源关联的所有数据库
     * 即使无法连接物理库，也要保证 MySQL 中的数据被删除
     */
    private void deleteAssociatedStores(long sourceId) {
        try {
            // 查询该数据源下的所有数据库
            List<AiVecStoreEntity> stores = getStoresBySourceId(sourceId);
            
            for (AiVecStoreEntity store : stores) {
                try {
                    // 尝试删除物理库中的集合（如果连不上物理库则忽略）
                    try {
                        dropPhysical(store);
                    } catch (Exception e) {
                        // 连不上物理库，记录日志但继续执行 MySQL 删除
                        log.warn("[AiVecSource] 物理库删除失败，继续删除 MySQL 数据 | Store: {} | 错误: {}", store.getId(), e.getMessage());
                    }
                    
                    // 删除 MySQL 中的数据库记录
                    removeStoreById(store.getId());
                } catch (Exception e) {
                    log.error("[AiVecSource] 删除关联数据库失败 | Store: {} | 错误: {}", store.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("[AiVecSource] 查询关联数据库失败 | Source: {} | 错误: {}", sourceId, e.getMessage());
        }
    }
    
    /**
     * 删除物理库中的集合（拷贝自 VectorStorePhysicalHandler 的逻辑）
     */
    private void dropPhysical(AiVecStoreEntity snapshot) {
        if (Objects.isNull(snapshot) || Objects.isNull(snapshot.getSourceId()) || StringUtils.isBlank(snapshot.getCollectionName())) {
            return;
        }

        Optional<AiVecSourceEntity> sourceOpt = Optional.ofNullable(getById(snapshot.getSourceId()));
        if (sourceOpt.isEmpty()) {
            log.warn("[AiVecSource] 物理删除跳过 | 原因: 数据源 {} 不存在 | 集合: {}", snapshot.getSourceId(), snapshot.getCollectionName());
            return;
        }

        AiVecSourceEntity source = sourceOpt.get();
        // 尝试获取活跃连接，否则创建一次性连接
        VecSource vs = astroVecSourceFactory.tryGetActiveSource(source.getId()).orElse(null);
        boolean isDisposable = Objects.isNull(vs);
        if (isDisposable) {
            vs = astroVecSourceFactory.bindSource(source);
        }

        try {
            vs.openStore(snapshot).dropCollection();
            log.info("[AiVecSource] 物理集合已删除 | Collection: {}", snapshot.getCollectionName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[AiVecSource] 删除物理集合失败 | Store: {} | 错误: {}", snapshot.getId(), e.getMessage());
            throw new BusinessException(AstVecStoreErrorEnum.STORE_DELETE_FAILED, e.getMessage());
        } finally {
            if (isDisposable && Objects.nonNull(vs)) {
                shutdownQuietly(vs);
            }
        }
    }
    
    private void shutdownQuietly(VecSource vs) {
        try {
            vs.shutdown();
        } catch (Exception e) {
            log.warn("[AiVecSource] 一次性句柄关闭异常: {}", e.getMessage());
        }
    }
    
    /**
     * 根据数据源ID查询关联的数据库列表
     */
    private List<AiVecStoreEntity> getStoresBySourceId(long sourceId) {
        try {
            // 使用 aiVecStoreMapper 查询
            return aiVecStoreMapper.selectList(new LambdaQueryWrapper<AiVecStoreEntity>()
                    .eq(AiVecStoreEntity::getSourceId, sourceId));
        } catch (Exception e) {
            log.error("[AiVecSource] 查询关联数据库失败 | Source: {} | 错误: {}", sourceId, e.getMessage());
            return List.of();
        }
    }
    
    /**
     * 删除数据库记录
     */
    private void removeStoreById(Long storeId) {
        try {
            // 使用 aiVecStoreMapper 删除
            aiVecStoreMapper.deleteById(storeId);
        } catch (Exception e) {
            log.error("[AiVecSource] 删除数据库记录失败 | Store: {} | 错误: {}", storeId, e.getMessage());
        }
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
