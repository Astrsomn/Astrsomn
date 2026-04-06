package org.astrsomn.vector.qdrant.internal;

import org.astrsomn.core.common.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Qdrant HTTP REST 管理端：健康检查、创建/删除 collection（不依赖额外 SDK）。
 */
public final class QdrantAdminClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final String baseUrl;
    private final String apiKey;

    public QdrantAdminClient(QdrantConnectionParams params) {
        String scheme = params.useTls() ? "https" : "http";
        this.baseUrl = scheme + "://" + params.host() + ":" + params.restPort();
        this.apiKey = params.apiKey();
    }

    public void health() {
        get("/");
    }

    public void createCollection(String name, int dimension, String qdrantDistance) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("collection name is blank");
        }
        String body = """
                {
                  "vectors": {
                    "size": %d,
                    "distance": "%s"
                  }
                }
                """.formatted(dimension, escapeJson(qdrantDistance));
        put("/collections/" + urlEncode(name), body);
    }

    public void deleteCollection(String name) {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        delete("/collections/" + urlEncode(name));
    }

    /**
     * 按 point id 列表删除（id 与 LangChain4j 写入一致，多为 UUID 字符串）。
     */
    public void deletePointsByIds(String collection, List<String> pointIds) {
        if (StringUtils.isEmpty(collection) || pointIds == null || pointIds.isEmpty()) {
            return;
        }
        String ids = pointIds.stream()
                .filter(StringUtils::isNotEmpty)
                .map(id -> "\"" + escapeJson(id) + "\"")
                .collect(Collectors.joining(","));
        if (ids.isEmpty()) {
            return;
        }
        String body = "{\"points\":[" + ids + "]}";
        post("/collections/" + urlEncode(collection) + "/points/delete", body);
    }

    /**
     * 按 payload 中 {@code payloadKey} 等于 {@code value} 删除点（入库时需写入相同 payload 字段）。
     */
    public void deletePointsByPayloadMatch(String collection, String payloadKey, String value) {
        if (StringUtils.isEmpty(collection) || StringUtils.isEmpty(payloadKey)) {
            return;
        }
        String body = """
                {
                  "filter": {
                    "must": [
                      {
                        "key": "%s",
                        "match": { "value": "%s" }
                      }
                    ]
                  }
                }
                """.formatted(escapeJson(payloadKey), escapeJson(value == null ? "" : value));
        post("/collections/" + urlEncode(collection) + "/points/delete", body);
    }

    private void post(String path, String jsonBody) {
        send(base(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8)));
    }

    private void get(String path) {
        send(base(path).GET());
    }

    private void put(String path, String jsonBody) {
        send(base(path)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8)));
    }

    private void delete(String path) {
        send(base(path).DELETE());
    }

    private HttpRequest.Builder base(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(TIMEOUT);
        if (StringUtils.isNotEmpty(apiKey)) {
            b.header("api-key", apiKey);
        }
        return b;
    }

    private static void send(HttpRequest.Builder builder) {
        HttpClient client = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
        try {
            HttpRequest request = builder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int code = response.statusCode();
            if (code >= 200 && code < 300) {
                return;
            }
            if (code == 409) {
                return;
            }
            throw new IllegalStateException("Qdrant HTTP " + code + ": " + response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Qdrant request interrupted", e);
        } catch (IOException e) {
            throw new IllegalStateException("Qdrant request failed: " + e.getMessage(), e);
        }
    }

    private static String urlEncode(String name) {
        return java.net.URLEncoder.encode(name, StandardCharsets.UTF_8);
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
