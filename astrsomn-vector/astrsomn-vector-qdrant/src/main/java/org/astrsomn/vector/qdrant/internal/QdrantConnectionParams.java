package org.astrsomn.vector.qdrant.internal;

import org.astrsomn.core.common.entity.AiVecSourceEntity;
import org.astrsomn.core.common.util.StringUtils;

/**
 * 从 {@link AiVecSourceEntity} 解析连接参数；{@code CONFIG_JSON} 支持可选布尔 {@code useTls}、
 * 数值 {@code restPort} / {@code grpcPort}（未设置时 REST 默认 6333、gRPC 默认 6334）。
 */
public final class QdrantConnectionParams {

    private static final int DEFAULT_REST_PORT = 6333;
    private static final int DEFAULT_GRPC_PORT = 6334;

    private final String host;
    private final int restPort;
    private final int grpcPort;
    private final boolean useTls;
    private final String apiKey;

    private QdrantConnectionParams(String host, int restPort, int grpcPort, boolean useTls, String apiKey) {
        this.host = host;
        this.restPort = restPort;
        this.grpcPort = grpcPort;
        this.useTls = useTls;
        this.apiKey = apiKey;
    }

    public static QdrantConnectionParams from(AiVecSourceEntity source) {
        if (source == null || StringUtils.isEmpty(source.getHost())) {
            throw new IllegalArgumentException("AiVecSource.host is required");
        }
        String host = source.getHost().trim();
        int rest = parsePort(source.getPort(), DEFAULT_REST_PORT);
        boolean tls = false;
        int grpc = DEFAULT_GRPC_PORT;
        String json = StringUtils.defaultIfBlank(source.getConfigJson(), "");
        if (!json.isEmpty()) {
            tls = json.contains("\"useTls\":true") || json.contains("\"useTls\" : true");
            Integer rp = extractIntField(json, "restPort");
            if (rp != null && rp > 0) {
                rest = rp;
            }
            Integer gp = extractIntField(json, "grpcPort");
            if (gp != null && gp > 0) {
                grpc = gp;
            }
        }
        String apiKey = StringUtils.trimToNull(source.getToken());
        return new QdrantConnectionParams(host, rest, grpc, tls, apiKey);
    }

    private static Integer extractIntField(String json, String field) {
        String needle = "\"" + field + "\"";
        int i = json.indexOf(needle);
        if (i < 0) {
            return null;
        }
        int colon = json.indexOf(':', i + needle.length());
        if (colon < 0) {
            return null;
        }
        int j = colon + 1;
        while (j < json.length() && Character.isWhitespace(json.charAt(j))) {
            j++;
        }
        int k = j;
        while (k < json.length() && Character.isDigit(json.charAt(k))) {
            k++;
        }
        if (k == j) {
            return null;
        }
        return Integer.parseInt(json.substring(j, k));
    }

    private static int parsePort(String portStr, int defaultPort) {
        if (StringUtils.isEmpty(portStr)) {
            return defaultPort;
        }
        try {
            int p = Integer.parseInt(portStr.trim());
            return p > 0 ? p : defaultPort;
        } catch (NumberFormatException e) {
            return defaultPort;
        }
    }

    public String host() {
        return host;
    }

    public int restPort() {
        return restPort;
    }

    public int grpcPort() {
        return grpcPort;
    }

    /**
     * LangChain4j Qdrant 集成通常使用 gRPC 端口。
     */
    public int grpcOrRestPortForEmbeddingStore() {
        return grpcPort;
    }

    public boolean useTls() {
        return useTls;
    }

    public String apiKey() {
        return apiKey;
    }
}
