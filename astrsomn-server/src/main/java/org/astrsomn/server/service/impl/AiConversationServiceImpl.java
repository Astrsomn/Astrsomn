package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.conversation.AiConversationCreateRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationQueryRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationUpdateRequestDTO;
import org.astrsomn.core.common.dto.conversation.AiConversationResponseDTO;
import org.astrsomn.core.common.entity.AiConversationEntity;
import org.astrsomn.core.mapper.AiConversationMapper;
import org.astrsomn.server.service.AiConversationService;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversationEntity> implements AiConversationService {

    private final QueryEnvParamHelper queryEnvParamHelper;
    @Override
    public BaseResponse<String> create(AiConversationCreateRequestDTO request) {
        AiConversationEntity entity = new AiConversationEntity();
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
    public BaseResponse<AiConversationResponseDTO> detail(Long id) {
        AiConversationEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiConversationResponseDTO responseDTO = new AiConversationResponseDTO();
        BeanUtils.copyProperties(entity, responseDTO);
        return BaseResponse.success(responseDTO);
    }

    @Override
    public BaseResponse<String> update(AiConversationUpdateRequestDTO request) {
        AiConversationEntity entity = new AiConversationEntity();
        BeanUtils.copyProperties(request, entity);
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiConversationResponseDTO> queryPage(BasePageRequest<AiConversationQueryRequestDTO> request) {
        IPage<AiConversationResponseDTO> page = request.buildPage();
        AiConversationQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiConversationQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiConversationResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }
}
