package org.astrsomn.server.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.entity.AiAgentEntity;
import org.astrsomn.core.mapper.AiAgentMapper;
import org.astrsomn.core.common.dto.agent.AiAgentCreateRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentQueryRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentUpdateRequestDTO;
import org.astrsomn.core.common.dto.agent.AiAgentResponseDTO;
import org.astrsomn.server.service.AiAgentService;


import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class AiAgentServiceImpl extends ServiceImpl<AiAgentMapper, AiAgentEntity> implements AiAgentService {



    @Override
    public BaseResponse<String> create(AiAgentCreateRequestDTO request) {
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);
        boolean result = save(aiAgent);

        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<AiAgentResponseDTO> detail(Long longId) {
        AiAgentEntity aiAgent = getById(longId);
        if (aiAgent == null) {
            return BaseResponse.fail("Agent不存在", null);
        }
        AiAgentResponseDTO responseDTO = new AiAgentResponseDTO();
        BeanUtils.copyProperties(aiAgent, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> updateAgent(AiAgentUpdateRequestDTO request) {
        AiAgentEntity aiAgent = new AiAgentEntity();
        BeanUtils.copyProperties(request, aiAgent);
        boolean result = updateById(aiAgent);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiAgentResponseDTO> queryPage(BasePageRequest<AiAgentQueryRequestDTO> request) {
        IPage<AiAgentResponseDTO> page = request.buildPage();
        IPage<AiAgentResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
