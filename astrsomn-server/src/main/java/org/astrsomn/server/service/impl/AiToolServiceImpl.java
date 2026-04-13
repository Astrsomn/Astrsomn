package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.tool.AiToolCreateRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolQueryRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolResponseDTO;
import org.astrsomn.core.common.dto.tool.AiToolUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.exception.base.BusinessException;
import org.astrsomn.core.exception.constant.AiToolErrorEnum;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.server.service.AiToolService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiToolServiceImpl extends ServiceImpl<AiToolMapper, AiToolEntity> implements AiToolService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiToolCreateRequestDTO request) {
        AiToolEntity entity = new AiToolEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignToolKeyIfBlank(entity);
        boolean result = save(entity);
        if (!result) {
            throw new BusinessException(AiToolErrorEnum.TOOL_CREATE_FAILED);
        }
        return BaseResponse.success("创建成功");
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        if (!result) {
            throw new BusinessException(AiToolErrorEnum.TOOL_DELETE_FAILED);
        }
        return BaseResponse.success("删除成功");
    }

    @Override
    public BaseResponse<AiToolResponseDTO> detail(Long id) {
        AiToolEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(AiToolErrorEnum.TOOL_NOT_FOUND);
        }
        AiToolResponseDTO responseDTO = new AiToolResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiToolUpdateRequestDTO request) {
        if (request.getId() == null) {
            throw new BusinessException(AiToolErrorEnum.TOOL_PARAM_ERROR);
        }
        AiToolEntity existing = getById(request.getId());
        if (existing == null) {
            throw new BusinessException(AiToolErrorEnum.TOOL_NOT_FOUND);
        }
        AiToolEntity entity = new AiToolEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        if (!result) {
            throw new BusinessException(AiToolErrorEnum.TOOL_UPDATE_FAILED);
        }
        return BaseResponse.success("更新成功");
    }

    @Override
    public PageResponse<AiToolResponseDTO> queryPage(BasePageRequest<AiToolQueryRequestDTO> request) {
        IPage<AiToolResponseDTO> page = request.buildPage();
        AiToolQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiToolQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiToolResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
