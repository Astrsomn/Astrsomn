package com.astrsomn.vector.chroma.internal;

import com.astrsomn.api.runtime.common.entity.AiVecSourceEntity;
import com.astrsomn.api.runtime.common.langchain.extension.vector.support.AiVecSourceConnectionProperties;
import com.astrsomn.common.utils.StringUtils;
import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 使用与 {@link ChromaEmbeddingStores} 一致的基址与租户/库解析，对 Chroma REST 做集合存在性检查与删除（不经过 LangChain4j
 * {@code EmbeddingStore#removeAll}，避免删后重建空集合）。
 */
public final class ChromaCollectionAdmin {

    private static final String DEFAULT_TENANT = "default";
    private static final String DEFAULT_DATABASE = "default";

    private static final HttpClient HTTP =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    private ChromaCollectionAdmin() {
    }

    public static boolean collectionExists(AiVecSourceEntity sourceEntity, String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            return false;
        }
        ChromaApiVersion v = ChromaConfigSupport.readApiVersion(sourceEntity.getConfigJson());
        String base = normalizeBase(ChromaEmbeddingStores.resolveBaseUrl(sourceEntity));
        String name = collectionName.trim();
        int code = httpStatus("GET", collectionUrl(v, sourceEntity, base, name), sourceEntity);
        if (code >= 200 && code < 300) {
            return true;
        }
        if (code == 404) {
            return false;
        }
        if (code == 500 && v == ChromaApiVersion.V1) {
            return false;
        }
        throw new IllegalStateException("Chroma collection exists check failed: HTTP " + code);
    }

    /**
     * 删除集合；若集合已不存在则视为成功（幂等）。
     */
    public static void deleteCollection(AiVecSourceEntity sourceEntity, String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            return;
        }
        ChromaApiVersion v = ChromaConfigSupport.readApiVersion(sourceEntity.getConfigJson());
        String base = normalizeBase(ChromaEmbeddingStores.resolveBaseUrl(sourceEntity));
        String name = collectionName.trim();
        int code = httpStatus("DELETE", collectionUrl(v, sourceEntity, base, name), sourceEntity);
        if (code >= 200 && code < 300 || code == 404) {
            return;
        }
        throw new IllegalStateException("Chroma delete collection failed: HTTP " + code);
    }

    private static String normalizeBase(String baseUrl) {
        String b = baseUrl == null ? "" : baseUrl.trim();
        return b.endsWith("/") ? b : b + "/";
    }

    private static String collectionUrl(
            ChromaApiVersion v, AiVecSourceEntity sourceEntity, String base, String collectionName) {
        if (v == ChromaApiVersion.V2) {
            String tenant = resolveTenant(sourceEntity);
            String database = resolveDatabase(sourceEntity);
            return base
                    + "api/v2/tenants/"
                    + enc(tenant)
                    + "/databases/"
                    + enc(database)
                    + "/collections/"
                    + enc(collectionName);
        }
        return base + "api/v1/collections/" + enc(collectionName);
    }

    private static String resolveTenant(AiVecSourceEntity entity) {
        String t = ChromaConfigSupport.readTenantName(entity.getConfigJson());
        return StringUtils.isNotBlank(t) ? t.trim() : DEFAULT_TENANT;
    }

    private static String resolveDatabase(AiVecSourceEntity entity) {
        AiVecSourceConnectionProperties conn = AiVecSourceConnectionProperties.from(entity);
        String db = conn.getDatabaseName();
        return StringUtils.isNotBlank(db) ? db.trim() : DEFAULT_DATABASE;
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private static int httpStatus(String method, String url, AiVecSourceEntity entity) {
        try {
            HttpRequest.Builder b =
                    HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(30));
            if ("GET".equals(method)) {
                b.GET();
            } else if ("DELETE".equals(method)) {
                b.method("DELETE", HttpRequest.BodyPublishers.noBody());
            } else {
                throw new IllegalArgumentException(method);
            }
            b.header("Content-Type", "application/json");
            String token = AiVecSourceConnectionProperties.from(entity).getToken();
            if (StringUtils.isNotBlank(token)) {
                b.header("Authorization", "Bearer " + token.trim());
            }
            HttpResponse<Void> resp =
                    HTTP.send(b.build(), HttpResponse.BodyHandlers.discarding());
            return resp.statusCode();
        } catch (Exception e) {
            throw new IllegalStateException("Chroma HTTP " + method + " failed: " + url, e);
        }
    }
}
