package com.astrsomn.server.service.extension.guard;

import com.astrsomn.system.constant.SystemExtensionEnum;
import com.astrsomn.api.vector.entity.AiVecSourceEntity;
import com.astrsomn.system.entity.SystemExtensionEntity;
import com.astrsomn.common.base.BaseResponse;
import com.astrsomn.common.utils.StringUtils;
import com.astrsomn.starter.runtime.mapper.AstAiVecSourceMapper;
import com.astrsomn.starter.runtime.mapper.AstSystemExtensionMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 向量库扩展：撤销应用 / 卸载前校验是否仍有向量源引用该 {@code provider}（与 extensionKey 对齐）。
 */
@Component
@RequiredArgsConstructor
public class SystemExtensionVecGuard {

    private final AstSystemExtensionMapper astSystemExtensionMapper;
    private final AstAiVecSourceMapper aiVecSourceMapper;

    public BaseResponse<Void> assertNoVecSourcesUseProvider(Long extensionId) {
        SystemExtensionEntity ext = astSystemExtensionMapper.selectById(extensionId);
        if (ext == null) {
            return BaseResponse.fail("记录不存在", null);
        }
        if (!SystemExtensionEnum.ExtensionTypeEnum.VECTOR_STORE.getCode().equals(ext.getType())) {
            return BaseResponse.success(null);
        }
        String provider = StringUtils.trimToNull(ext.getExtensionKey());
        if (provider == null) {
            return BaseResponse.fail("无法解析 extensionKey，无法校验向量源表", null);
        }

        Long count =
                aiVecSourceMapper.selectCount(
                        new LambdaQueryWrapper<AiVecSourceEntity>().eq(AiVecSourceEntity::getExtensionCode, provider));
        if (count != null && count > 0) {
            return BaseResponse.fail(
                    "仍存在引用该驱动的向量源（共 " + count + " 条），请先删除或调整向量源后再操作。", null);
        }
        return BaseResponse.success(null);
    }
}

