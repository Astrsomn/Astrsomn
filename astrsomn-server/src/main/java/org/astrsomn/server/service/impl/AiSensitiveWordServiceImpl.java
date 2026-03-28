package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordCreateRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordQueryRequestDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordResponseDTO;
import org.astrsomn.core.common.dto.sensitiveword.AiSensitiveWordUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiSensitiveWordEntity;
import org.astrsomn.core.mapper.AiSensitiveWordMapper;
import org.astrsomn.server.service.AiSensitiveWordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AiSensitiveWordServiceImpl extends ServiceImpl<AiSensitiveWordMapper, AiSensitiveWordEntity>
        implements AiSensitiveWordService {

    @Override
    public BaseResponse<String> create(AiSensitiveWordCreateRequestDTO request) {
        AiSensitiveWordEntity entity = new AiSensitiveWordEntity();
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
    public BaseResponse<AiSensitiveWordResponseDTO> detail(Long id) {
        AiSensitiveWordEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiSensitiveWordResponseDTO responseDTO = new AiSensitiveWordResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiSensitiveWordUpdateRequestDTO request) {
        AiSensitiveWordEntity entity = new AiSensitiveWordEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiSensitiveWordResponseDTO> queryPage(BasePageRequest<AiSensitiveWordQueryRequestDTO> request) {
        IPage<AiSensitiveWordResponseDTO> page = request.buildPage();
        IPage<AiSensitiveWordResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
