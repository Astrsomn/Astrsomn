package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.prompt.AiPromptCreateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptQueryRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptUpdateRequestDTO;
import org.astrsomn.core.common.dto.prompt.AiPromptResponseDTO;
import org.astrsomn.core.common.entity.AiPromptEntity;
import org.astrsomn.core.mapper.AiPromptMapper;
import org.astrsomn.server.service.AiPromptService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiPromptServiceImpl extends ServiceImpl<AiPromptMapper, AiPromptEntity> implements AiPromptService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;

    @Override
    public BaseResponse<String> create(AiPromptCreateRequestDTO request) {
        AiPromptEntity entity = new AiPromptEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignPromptKeyIfBlank(entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<AiPromptResponseDTO> detail(Long id) {
        AiPromptEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiPromptResponseDTO responseDTO = new AiPromptResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiPromptUpdateRequestDTO request) {
        AiPromptEntity entity = new AiPromptEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiPromptResponseDTO> queryPage(BasePageRequest<AiPromptQueryRequestDTO> request) {
        IPage<AiPromptResponseDTO> page = request.buildPage();
        IPage<AiPromptResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
