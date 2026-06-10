package com.astrsomn.provider.anthropic;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import com.astrsomn.api.runtime.common.model.ModelParamDefaults;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnthropicModelApiClient {
    private static final String DEFAULT_BASE_URL = "https://api.anthropic.com";
    private final String baseUrl;
    private final HttpClient httpClient;

    public AnthropicModelApiClient() { this(DEFAULT_BASE_URL); }
    public AnthropicModelApiClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl.trim() : DEFAULT_BASE_URL;
        this.httpClient = HttpClient.newHttpClient();
    }

    @SuppressWarnings("unchecked")
    public List<ProviderModelDTO> listModels(String apiKey, String apiSecret) {
        if (apiKey == null || apiKey.isBlank()) return Collections.emptyList();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/models"))
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
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

                ProviderModelDTO dto = new ProviderModelDTO();
                dto.setModelKey(modelId);
                dto.setModelName(modelId);
                dto.setModelType("chat");

                Object created = item.get("created_at");
                if (created instanceof String s) {
                    try { dto.setReleasedAt(LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)); }
                    catch (Exception ignored) {}
                }

                dto.setCapabilities(inferCaps(modelId));
                dto.setParams(inferParams(modelId));
                models.add(dto);
            }
            return models;
        } catch (Exception e) { return Collections.emptyList(); }
    }

    private static List<String> inferCaps(String modelId) {
        List<String> caps = new ArrayList<>(List.of("STREAMING", "TOOLS"));
        if (modelId.contains("claude-3") || modelId.contains("claude-4")) caps.add("VISION");
        if (modelId.contains("claude-3-5") || modelId.contains("claude-3-opus")) caps.add("DEEP_REASONING");
        if (!modelId.contains("claude-3-haiku")) caps.add("JSON_MODE");
        return caps;
    }

    private static List<ProviderModelDTO.ParamDef> inferParams(String modelId) {
        List<ProviderModelDTO.ParamDef> params = new ArrayList<>(ModelParamDefaults.chatParams());
        params.replaceAll(p -> {
            if ("max_tokens".equals(p.getId()))
                return ModelParamDefaults.param("max_tokens", "Max Tokens", "max_tokens", "8192", "1-200k", true);
            return p;
        });
        if (modelId.contains("claude-3-5") || modelId.contains("claude-3-opus") || modelId.contains("claude-4")) {
            params.add(ModelParamDefaults.param("extended_thinking", "Extended Thinking", "extended_thinking", "", "boolean", false));
        }
        return params;
    }
}
