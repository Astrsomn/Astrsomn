package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.template.AiTemplateCreateRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateQueryRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateUpdateRequestDTO;
import org.astrsomn.core.common.dto.template.AiTemplateResponseDTO;
import org.astrsomn.core.common.entity.AiTemplateEntity;
import org.astrsomn.core.mapper.AiTemplateMapper;
import org.astrsomn.server.service.AiTemplateService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiTemplateServiceImpl extends ServiceImpl<AiTemplateMapper, AiTemplateEntity> implements AiTemplateService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    @Override
    public BaseResponse<String> create(AiTemplateCreateRequestDTO request) {
        AiTemplateEntity entity = new AiTemplateEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<AiTemplateResponseDTO> detail(Long id) {
        AiTemplateEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiTemplateResponseDTO responseDTO = new AiTemplateResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiTemplateUpdateRequestDTO request) {
        AiTemplateEntity entity = new AiTemplateEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiTemplateResponseDTO> queryPage(BasePageRequest<AiTemplateQueryRequestDTO> request) {
        IPage<AiTemplateResponseDTO> page = request.buildPage();
        AiTemplateQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiTemplateQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiTemplateResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
