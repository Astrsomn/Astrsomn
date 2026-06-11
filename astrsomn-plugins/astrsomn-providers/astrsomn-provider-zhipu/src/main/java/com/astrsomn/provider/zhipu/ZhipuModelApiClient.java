package com.astrsomn.provider.zhipu;

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

public class ZhipuModelApiClient {
    private static final String DEFAULT_BASE_URL = "https://open.bigmodel.cn/api/paas/v4";
    private final String baseUrl;
    private final HttpClient httpClient;

    public ZhipuModelApiClient() { this(DEFAULT_BASE_URL); }
    public ZhipuModelApiClient(String baseUrl) {
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

            List<Map<String, Object>> dataList = null;

            // Format 1: { "data": [...] } (OpenAI-compatible)
            Object rawData = body.get("data");
            if (rawData instanceof List) {
                dataList = (List<Map<String, Object>>) rawData;
            }

            // Format 2: { "items": [...] }
            if (dataList == null || dataList.isEmpty()) {
                rawData = body.get("items");
                if (rawData instanceof List) {
                    dataList = (List<Map<String, Object>>) rawData;
                }
            }

            // Format 3: { "result": { "models": [...] } }
            if (dataList == null || dataList.isEmpty()) {
                Map<String, Object> result = (Map<String, Object>) body.get("result");
                if (result != null && result.containsKey("models")) {
                    dataList = (List<Map<String, Object>>) result.get("models");
                }
            }

            if (dataList == null || dataList.isEmpty()) return Collections.emptyList();

            List<ProviderModelDTO> models = new ArrayList<>();
            for (Map<String, Object> item : dataList) {
                String modelId = (String) item.get("id");
                if (modelId == null) modelId = (String) item.get("model");
                if (modelId == null) modelId = (String) item.get("model_id");
                if (modelId == null) modelId = (String) item.get("model_name");
                if (modelId == null || modelId.isEmpty()) continue;

                String displayName = (String) item.get("model_name");

                String modelType = inferModelType(modelId);
                ProviderModelDTO dto = new ProviderModelDTO();
                dto.setModelKey(modelId);
                dto.setModelName(displayName != null ? displayName : modelId);
                dto.setDescription((String) item.get("description"));
                dto.setModelType(modelType);
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
        if (id.contains("image")) return "image";
        return "chat";
    }
}
