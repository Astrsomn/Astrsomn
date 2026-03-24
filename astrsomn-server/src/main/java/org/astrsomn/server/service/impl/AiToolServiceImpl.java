package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.tool.AiToolCreateRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolQueryRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolUpdateRequestDTO;
import org.astrsomn.core.common.dto.tool.AiToolResponseDTO;
import org.astrsomn.core.common.entity.AiToolEntity;
import org.astrsomn.core.mapper.AiToolMapper;
import org.astrsomn.server.service.AiToolService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AiToolServiceImpl extends ServiceImpl<AiToolMapper, AiToolEntity> implements AiToolService {
    @Override
    public BaseResponse<String> create(AiToolCreateRequestDTO request) {
        AiToolEntity entity = new AiToolEntity();
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
    public BaseResponse<AiToolResponseDTO> detail(Long id) {
        AiToolEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiToolResponseDTO responseDTO = new AiToolResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiToolUpdateRequestDTO request) {
        AiToolEntity entity = new AiToolEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiToolResponseDTO> queryPage(BasePageRequest<AiToolQueryRequestDTO> request) {
        IPage<AiToolResponseDTO> page = request.buildPage();
        IPage<AiToolResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
