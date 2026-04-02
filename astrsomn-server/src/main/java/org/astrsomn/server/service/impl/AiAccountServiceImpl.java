package org.astrsomn.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.util.StringUtils;
import org.astrsomn.core.common.base.BasePageRequest;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.base.PageResponse;
import org.astrsomn.core.common.dto.account.AiAccountCreateRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountQueryRequestDTO;
import org.astrsomn.core.common.dto.account.AiAccountResponseDTO;
import org.astrsomn.core.common.dto.account.AiAccountUpdateRequestDTO;
import org.astrsomn.core.common.entity.AiAccountEntity;
import org.astrsomn.core.common.entity.AiModelEntity;
import org.astrsomn.core.mapper.AiAccountMapper;
import org.astrsomn.core.mapper.AiModelMapper;
import org.astrsomn.server.service.AiAccountService;
import org.astrsomn.server.service.support.BizResourceKeyAssignHelper;
import org.astrsomn.server.service.support.QueryEnvParamHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AiAccountServiceImpl extends ServiceImpl<AiAccountMapper, AiAccountEntity> implements AiAccountService {

    private final BizResourceKeyAssignHelper bizResourceKeyAssignHelper;
    private final AiModelMapper aiModelMapper;
    private final QueryEnvParamHelper queryEnvParamHelper;

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
        dto.setAccountKeyImmutable(isAccountKeyReferencedByModel(entity.getAccountKey(), entity.getEnvCode()));
        return BaseResponse.success(dto);
    }

    @Override
    public BaseResponse<String> update(AiAccountUpdateRequestDTO request) {
        if (request.getId() == null) {
            return BaseResponse.fail("ID不能为空", null);
        }
        AiAccountEntity existing = getById(request.getId());
        if (existing == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        AiAccountEntity entity = new AiAccountEntity();
        BeanUtils.copyProperties(request, entity);
        if (isAccountKeyReferencedByModel(existing.getAccountKey(), existing.getEnvCode())) {
            entity.setAccountKey(existing.getAccountKey());
        } else {
            bizResourceKeyAssignHelper.assignAccountKeyIfBlank(entity, entity.getId());
        }
        boolean result = updateById(entity);
        return result ? BaseResponse.success("更新成功") : BaseResponse.fail("更新失败", null);
    }

    @Override
    public PageResponse<AiAccountResponseDTO> queryPage(BasePageRequest<AiAccountQueryRequestDTO> request) {
        IPage<AiAccountResponseDTO> page = request.buildPage();
        AiAccountQueryRequestDTO param = request.getParam();
        if (param == null) {
            param = new AiAccountQueryRequestDTO();
        }
        queryEnvParamHelper.stampEffectiveEnv(param);
        IPage<AiAccountResponseDTO> result = baseMapper.queryPage(page, param);
        return PageResponse.buildResponse(result);
    }

    /**
     * 同环境下是否存在 AI_MODEL 引用该 accountKey。
     */
    private boolean isAccountKeyReferencedByModel(String accountKey, String envCode) {
        if (StringUtils.isBlank(accountKey) || StringUtils.isBlank(envCode)) {
            return false;
        }
        return aiModelMapper.selectCount(
                        new LambdaQueryWrapper<AiModelEntity>()
                                .eq(AiModelEntity::getAccountKey, accountKey.trim())
                                .eq(AiModelEntity::getEnvCode, envCode.trim()))
                > 0;
    }
}
