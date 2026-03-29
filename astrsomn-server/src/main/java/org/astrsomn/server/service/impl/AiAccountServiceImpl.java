package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.account.AiAccountCreateRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import org.astrsomn.core.common.dto.account.AiAccountUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.server.service.AiAccountService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiAccountServiceImpl extends ServiceImpl<AiAccountMapper, AiAccountEntity> implements AiAccountService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;

    @Override
    public BaseResponse<String> create(AiAccountCreateRequestDTO request) {
        AiAccountEntity entity = new AiAccountEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignAccountKeyIfBlank(entity);
        boolean result = save(entity);
        return result ? BaseResponse.success("创建成功") : BaseResponse.fail("创建失败", null);
    }

    @Override
    public BaseResponse<String> delete(long[] ids) {
        boolean result = removeByIds(Arrays.asList(Arrays.stream(ids).boxed().toArray(Long[]::new)));
        return result ? BaseResponse.success("删除成功") : BaseResponse.fail("删除失败", null);
    }

    @Override
    public BaseResponse<AiAccountResponseDTO> detail(Long id) {
        AiAccountEntity entity = getById(id);
        if (entity == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiAccountResponseDTO dto = new AiAccountResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> update(AiAccountUpdateRequestDTO request) {
        AiAccountEntity entity = new AiAccountEntity();
        BeanUtils.copyProperties(request, entity);
        bizResourceKeyAssignHelper.assignAccountKeyIfBlank(entity, entity.getId());
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request) {
        IPage<AiAccountResponseDTO> page = request.buildPage();
        IPage<AiAccountResponseDTO> result = baseMapper.queryPage(page, request.getParam());
        return PageResponse.buildResponse(result);
    }
}
