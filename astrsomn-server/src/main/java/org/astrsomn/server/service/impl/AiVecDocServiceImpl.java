package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocCreateRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocQueryRequestDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocResponseDTO;
import org.astrsomn.core.common.dto.vecdoc.AiVecDocUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiVecDocEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AstVecDocErrorEnum;
import org.astrsomn.core.mapper.AiVecDocMapper;
import org.astrsomn.server.service.AiVecDocService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiVecDocServiceImpl extends ServiceImpl<AiVecDocMapper, AiVecDocEntity> implements AiVecDocService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiVecDocCreateRequestDTO request) {
        AiVecDocEntity entity = new AiVecDocEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        if (ids == null || ids.length == 0) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        for (long id : ids) {
            removeById(id);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<String> update(AiVecDocUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_PARAM_ERROR);
        }
        AiVecDocEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocEntity entity = new AiVecDocEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiVecDocResponseDTO> queryPage(BasePageRequest<AiVecDocQueryRequestDTO> request) {
        IPage<AiVecDocResponseDTO> page = request.buildPage();
        AiVecDocQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiVecDocQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiVecDocResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    @Override
    public BaseResponse<AiVecDocResponseDTO> detail(Long id) {
        AiVecDocEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AstVecDocErrorEnum.DOC_NOT_FOUND);
        }
        AiVecDocResponseDTO responseDTO = new AiVecDocResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }
}
