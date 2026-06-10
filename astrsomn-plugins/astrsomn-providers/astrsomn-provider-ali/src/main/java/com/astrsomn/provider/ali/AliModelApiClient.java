package com.astrsomn.provider.ali;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import com.astrsomn.api.runtime.common.model.ModelParamDefaults;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AliModelApiClient {
    private static final String DEFAULT_BASE_URL = "https://dashscope.aliyuncs.com";
    private final String baseUrl;
    private final HttpClient httpClient;

    public AliModelApiClient() { this(DEFAULT_BASE_URL); }
    public AliModelApiClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl.trim() : DEFAULT_BASE_URL;
        this.httpClient = HttpClient.newHttpClient();
    }

    @SuppressWarnings("unchecked")
    public List<ProviderModelDTO> listModels(String apiKey, String apiSecret) {
        if (apiKey == null || apiKey.isBlank()) return Collections.emptyList();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/models"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) return Collections.emptyList();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> body = mapper.readValue(response.body(), Map.class);

            List<Map<String, Object>> dataList = null;
            // Format 1: { "data": { "models": [...] } }
            Map<String, Object> data = (Map<String, Object>) body.get("data");
            if (data != null && data.containsKey("models")) {
                dataList = (List<Map<String, Object>>) data.get("models");
            }
            // Format 2: { "data": [ ... ] }
            if (dataList == null) {
                Object raw = body.get("data");
                if (raw instanceof List) dataList = (List<Map<String, Object>>) raw;
            }
            // Format 3: { "output": { "models": [...] } }
            if (dataList == null) {
                Map<String, Object> out = (Map<String, Object>) body.get("output");
                if (out != null && out.containsKey("models")) dataList = (List<Map<String, Object>>) out.get("models");
            }
            if (dataList == null) dataList = new ArrayList<>();

            List<ProviderModelDTO> models = new ArrayList<>();
            for (Map<String, Object> item : dataList) {
                String modelId = (String) item.get("model_id");
                if (modelId == null) modelId = (String) item.get("id");
                if (modelId == null) modelId = (String) item.get("model_name");
                if (modelId == null || modelId.isEmpty()) continue;

                String modelType = inferModelType(modelId);
                String displayName = (String) item.get("model_name");

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
        if (id.contains("embedding") || id.contains("text-embedding")) return "embedding";
        if (id.contains("image") || id.contains("dall") || id.contains("wanx")) return "image";
        return "chat";
    }
}
