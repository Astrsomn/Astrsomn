package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiSensitiveWordEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AiSensitiveWordErrorEnum;
import org.astrsomn.core.mapper.AiSensitiveWordMapper;
import org.astrsomn.server.service.AiSensitiveWordService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiSensitiveWordServiceImpl extends ServiceImpl<AiSensitiveWordMapper, AiSensitiveWordEntity>
        implements AiSensitiveWordService {

    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiSensitiveWordCreateRequestDTO request) {
        AiSensitiveWordEntity entity = new AiSensitiveWordEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiSensitiveWordErrorEnum.SENSITIVE_WORD_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiSensitiveWordErrorEnum.SENSITIVE_WORD_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiSensitiveWordResponseDTO> detail(Long id) {
        AiSensitiveWordEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiSensitiveWordErrorEnum.SENSITIVE_WORD_NOT_FOUND);
        }
        AiSensitiveWordResponseDTO responseDTO = new AiSensitiveWordResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiSensitiveWordUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiSensitiveWordErrorEnum.SENSITIVE_WORD_PARAM_ERROR);
        }
        AiSensitiveWordEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiSensitiveWordErrorEnum.SENSITIVE_WORD_NOT_FOUND);
        }
        AiSensitiveWordEntity entity = new AiSensitiveWordEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiSensitiveWordErrorEnum.SENSITIVE_WORD_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiSensitiveWordResponseDTO> queryPage(BasePageRequest<AiSensitiveWordQueryRequestDTO> request) {
        IPage<AiSensitiveWordResponseDTO> page = request.buildPage();
        AiSensitiveWordQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiSensitiveWordQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiSensitiveWordResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
