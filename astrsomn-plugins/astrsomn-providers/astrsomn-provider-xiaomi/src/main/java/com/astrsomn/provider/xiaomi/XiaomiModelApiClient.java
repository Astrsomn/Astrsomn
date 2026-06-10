package com.astrsomn.provider.xiaomi;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import com.astrsomn.api.runtime.common.model.ModelParamDefaults;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class XiaomiModelApiClient {
    private static final String DEFAULT_BASE_URL = "https://api.minimax.chat/v1";
    private final String baseUrl;
    private final HttpClient httpClient;

    public XiaomiModelApiClient() { this(DEFAULT_BASE_URL); }
    public XiaomiModelApiClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl.trim() : DEFAULT_BASE_URL;
        this.httpClient = HttpClient.newHttpClient();
    }

    @SuppressWarnings("unchecked")
    public List<ProviderModelDTO> listModels(String apiKey, String apiSecret) {
        if (apiKey == null || apiKey.isBlank()) return Collections.emptyList();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/models"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) return Collections.emptyList();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> body = mapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) body.get("data");
            if (dataList == null || dataList.isEmpty()) return Collections.emptyList();

            List<ProviderModelDTO> models = new ArrayList<>();
            for (Map<String, Object> item : dataList) {
                String modelId = (String) item.get("id");
                if (modelId == null || modelId.isEmpty()) continue;

                Object createdObj = item.get("created");
                LocalDateTime releasedAt = null;
                if (createdObj instanceof Number num) {
                    releasedAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(num.longValue()), ZoneId.systemDefault());
                }

                String modelType = inferModelType(modelId);
                ProviderModelDTO dto = new ProviderModelDTO();
                dto.setModelKey(modelId);
                dto.setModelName(modelId);
                dto.setModelType(modelType);
                dto.setReleasedAt(releasedAt);
                dto.setCapabilities(ModelParamDefaults.inferCapsFromId(modelId, modelType));
                dto.setParams(ModelParamDefaults.inferParamsFromId(modelId, modelType));
                models.add(dto);
            }
            return models;
        } catch (Exception e) { return Collections.emptyList(); }
    }

    private static String inferModelType(String modelId) {
        String id = modelId.toLowerCase();
        if (id.contains("embedding")) return "embedding";
        return "chat";
    }
}
