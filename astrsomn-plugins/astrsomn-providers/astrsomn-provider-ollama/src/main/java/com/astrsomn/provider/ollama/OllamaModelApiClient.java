package com.astrsomn.provider.ollama;

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

public class OllamaModelApiClient {
    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private final String baseUrl;
    private final HttpClient httpClient;

    public OllamaModelApiClient() { this(DEFAULT_BASE_URL); }
    public OllamaModelApiClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl.trim() : DEFAULT_BASE_URL;
        this.httpClient = HttpClient.newHttpClient();
    }

    @SuppressWarnings("unchecked")
    public List<ProviderModelDTO> listModels(String apiKey, String apiSecret) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/tags"))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) return Collections.emptyList();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> body = mapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> modelsList = (List<Map<String, Object>>) body.get("models");
            if (modelsList == null || modelsList.isEmpty()) return Collections.emptyList();

            List<ProviderModelDTO> result = new ArrayList<>();
            for (Map<String, Object> item : modelsList) {
                String name = (String) item.get("name");
                if (name == null) continue;
                String modelType = name.contains("embed") ? "embedding" : "chat";

                ProviderModelDTO dto = new ProviderModelDTO();
                dto.setModelKey(name);
                dto.setModelName(name);
                dto.setModelType(modelType);
                dto.setCapabilities(ModelParamDefaults.capsByType(modelType));
                dto.setParams(ModelParamDefaults.paramsByType(modelType));
                result.add(dto);
            }
            return result;
        } catch (Exception e) { return Collections.emptyList(); }
    }
}
