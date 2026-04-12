package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreCreateRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreQueryRequestDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreResponseDTO;
import org.astrsomn.core.common.dto.vecstore.AiVecStoreUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecStoreEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecStoreErrorEnum;
import org.astrsomn.core.mapper.AiVecStoreMapper;
import org.astrsomn.server.service.AiVecStoreService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiVecStoreServiceImpl extends ServiceImpl<AiVecStoreMapper, AiVecStoreEntity> implements AiVecStoreService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiVecStoreCreateRequestDTO request) {
        AiVecStoreEntity entity = new AiVecStoreEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        for (long id : ids) {
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiVecStoreUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_PARAM_ERROR);
        }
        AiVecStoreEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        AiVecStoreEntity entity = new AiVecStoreEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecStoreResponseDTO> queryPage(BasePageRequest<AiVecStoreQueryRequestDTO> request) {
        IPage<AiVecStoreResponseDTO> page = request.buildPage();
        AiVecStoreQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecStoreQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecStoreResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<AiVecStoreResponseDTO> detail(Long id) {
        AiVecStoreResponseDTO responseDTO = baseMapper.selectDetailDtoById(id);
        if (responseDTO == null) {
            throw new BusinessException(AstVecStoreErrorEnum.STORE_NOT_FOUND);
        }
        return BaseResponse.success(responseDTO);
    }
}
