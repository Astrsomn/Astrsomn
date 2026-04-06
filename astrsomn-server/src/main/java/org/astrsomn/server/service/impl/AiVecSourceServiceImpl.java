package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceCreateRequestDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceQueryRequestDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceResponseDTO;
import org.astrsomn.core.common.dto.vecsource.AiVecSourceUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecSourceErrorEnum;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.server.service.AiVecSourceService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiVecSourceServiceImpl extends ServiceImpl<AiVecSourceMapper, AiVecSourceEntity> implements AiVecSourceService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiVecSourceCreateRequestDTO request) {
        AiVecSourceEntity entity = new AiVecSourceEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecSourceErrorEnum.SOURCE_PARAM_ERROR);
        }
        for (long id : ids) {
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
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecSourceResponseDTO> queryPage(BasePageRequest<AiVecSourceQueryRequestDTO> request) {
        IPage<AiVecSourceResponseDTO> page = request.buildPage();
        AiVecSourceQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecSourceQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecSourceResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
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
}
