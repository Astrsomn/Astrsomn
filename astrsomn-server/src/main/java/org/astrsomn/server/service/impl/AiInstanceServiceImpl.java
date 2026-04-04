package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.instance.AiInstanceCreateRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceQueryRequestDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceResponseDTO;
import org.astrsomn.core.common.dto.instance.AiInstanceUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiInstanceEntity;
import org.astrsomn.core.mapper.AiInstanceMapper;
import org.astrsomn.server.service.AiInstanceService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiInstanceServiceImpl extends ServiceImpl<AiInstanceMapper, AiInstanceEntity> implements AiInstanceService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;
    private final QueryEnvParamHelper queryEnvParamHelper;

    @Override
    public BaseResponse<String> create(AiInstanceCreateRequestDTO request) {
        if (StringUtils.isBlank(request.getModelKey())) {
            return BaseResponse.fail("请选择关联模型（modelKey）", null);
        }
        AiInstanceEntity entity = new AiInstanceEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignInstanceKeyIfBlank(entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<AiInstanceResponseDTO> detail(Long id) {
        AiInstanceResponseDTO dto = baseMapper.selectDetailDtoById(id);
        if (dto == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> update(AiInstanceUpdateRequestDTO request) {
        if (request.getId() == null) {
            return BaseResponse.fail("ID不能为空", null);
        }
        AiInstanceEntity existing = getById(request.getId());
        if (existing == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiInstanceEntity entity = new AiInstanceEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignInstanceKeyIfBlank(entity);
        entity.setModelKey(existing.getModelKey());
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiInstanceResponseDTO> queryPage(BasePageRequest<AiInstanceQueryRequestDTO> request) {
        IPage<AiInstanceResponseDTO> page = request.buildPage();
        AiInstanceQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiInstanceQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiInstanceResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
