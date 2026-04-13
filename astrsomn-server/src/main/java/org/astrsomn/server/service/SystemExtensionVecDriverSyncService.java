package org.astrsomn.server.service;

import org.astrsomn.core.common.entity.SystemExtensionEntity;

/**
 * 向量库扩展应用时，将插件内 {@link org.astrsomn.core.common.langchain.extension.vector.VecDriver#getDriverEntity()}
 * 同步到 {@code AI_VEC_DRIVER}（按 {@code provider} 幂等）。
 */
public interface SystemExtensionVecDriverSyncService {

    void upsertFromExtension(SystemExtensionEntity extension);

    /**
     * 撤销应用或卸载时删除该 provider 对应的驱动元数据行（调用方已做引用校验）。
     */
    void removeDriverRowForProvider(String providerCode);
}
