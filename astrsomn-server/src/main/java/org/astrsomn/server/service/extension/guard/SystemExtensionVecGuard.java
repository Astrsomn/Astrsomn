package org.astrsomn.server.service.extension.guard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.astrsomn.core.common.base.BaseResponse;
import org.astrsomn.core.common.constant.SystemExtensionEnum;
import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.entity.SystemExtensionEntity;
import org.astrsomn.core.common.utils.StringUtils;
import org.astrsomn.core.mapper.AiVecSourceMapper;
import org.astrsomn.core.mapper.SystemExtensionMapper;
import org.springframework.stereotype.Component;

/**
 * 向量库扩展：撤销应用 / 卸载前校验是否仍有向量源引用该 {@code provider}（与 extensionKey 对齐）。
 */
@Component
@RequiredArgsConstructor
public class SystemExtensionVecGuard {

    private final SystemExtensionMapper systemExtensionMapper;
    private final AiVecSourceMapper aiVecSourceMapper;

    public BaseResponse<Void> assertNoVecSourcesUseProvider(Long extensionId) {
        SystemExtensionEntity ext = systemExtensionMapper.selectById(extensionId);
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
                        new LambdaQueryWrapper<AiVecSourceEntity>().eq(AiVecSourceEntity::getProvider, provider));
        if (count != null && count > 0) {
            return BaseResponse.fail(
                    "仍存在引用该驱动的向量源（共 " + count + " 条），请先删除或调整向量源后再操作。", null);
        }
        return BaseResponse.success(null);
    }
}

