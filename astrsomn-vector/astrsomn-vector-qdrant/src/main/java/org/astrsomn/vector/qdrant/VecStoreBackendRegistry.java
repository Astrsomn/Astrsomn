package org.astrsomn.vector.qdrant;

import org.astrsomn.core.common.langchain.extension.VecStoreBackend;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 合并 SPI 与（可选）Spring 注册的 {@link VecStoreBackend}；Spring Bean 后注册可覆盖 SPI。
 */
public class VecStoreBackendRegistry {

    private final Map<String, VecStoreBackend> byKey = new ConcurrentHashMap<>();

    public VecStoreBackendRegistry() {
        ServiceLoader.load(VecStoreBackend.class)
                .forEach(b -> byKey.putIfAbsent(b.getExtensionKey(), b));
    }

    public void register(VecStoreBackend backend) {
        if (backend != null) {
            byKey.put(backend.getExtensionKey(), backend);
        }
    }

    public VecStoreBackend getRequired(String provider) {
        if (provider == null || provider.isBlank()) {
            throw new IllegalStateException("向量 provider 为空");
        }
        VecStoreBackend b = byKey.get(provider.trim());
        if (b == null) {
            throw new IllegalStateException("未注册向量后端实现: " + provider + "，请引入对应 astrsomn-vector-* 模块");
        }
        return b;
    }
}
