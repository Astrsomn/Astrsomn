package com.astrsomn.provider.qianfan;

import com.astrsomn.api.runtime.common.dto.model.ProviderModelDTO;
import com.astrsomn.api.runtime.common.model.ModelParamDefaults;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QianfanModelApiClient {
    private static final String DEFAULT_BASE_URL = "https://aip.baidubce.com";
    private final String baseUrl;
    private final HttpClient httpClient;

    public QianfanModelApiClient() { this(DEFAULT_BASE_URL); }
    public QianfanModelApiClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl.trim() : DEFAULT_BASE_URL;
        this.httpClient = HttpClient.newHttpClient();
    }

    @SuppressWarnings("unchecked")
    public List<ProviderModelDTO> listModels(String apiKey, String apiSecret) {
        if (apiKey == null || apiKey.isBlank() || apiSecret == null || apiSecret.isBlank()) {
            return Collections.emptyList();
        }
        try {
            // OAuth 2.0 client credentials flow
            String tokenBody = "grant_type=client_credentials&client_id="
                    + URLEncoder.encode(apiKey, StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(apiSecret, StandardCharsets.UTF_8);

            HttpRequest tokenReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/oauth/2.0/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(tokenBody))
                    .build();
            HttpResponse<String> tokenRes = httpClient.send(tokenReq, HttpResponse.BodyHandlers.ofString());
            if (tokenRes.statusCode() < 200 || tokenRes.statusCode() >= 300) return Collections.emptyList();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> tokenMap = mapper.readValue(tokenRes.body(), Map.class);
            String accessToken = (String) tokenMap.get("access_token");
            if (accessToken == null) return Collections.emptyList();

            // List models with access token
            HttpRequest listReq = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v2/models?access_token=" + accessToken))
                    .GET().build();
            HttpResponse<String> listRes = httpClient.send(listReq, HttpResponse.BodyHandlers.ofString());
            if (listRes.statusCode() < 200 || listRes.statusCode() >= 300) return Collections.emptyList();

            Map<String, Object> listBody = mapper.readValue(listRes.body(), Map.class);

            // Try multiple response formats
            List<Map<String, Object>> dataList = null;
            Object data = listBody.get("data");
            if (data instanceof List) dataList = (List<Map<String, Object>>) data;
            if (dataList == null) {
                Map<String, Object> result = (Map<String, Object>) listBody.get("result");
                if (result != null) {
                    Object m = result.get("models");
                    if (m instanceof List) dataList = (List<Map<String, Object>>) m;
                }
            }
            if (dataList == null) {
                Object r = listBody.get("result");
                if (r instanceof List) dataList = (List<Map<String, Object>>) r;
            }
            if (dataList == null) dataList = new ArrayList<>();

            List<ProviderModelDTO> models = new ArrayList<>();
            for (Map<String, Object> item : dataList) {
                String modelId = (String) item.get("modelId");
                if (modelId == null) modelId = (String) item.get("model_id");
                if (modelId == null) modelId = (String) item.get("id");
                if (modelId == null) continue;

                String modelType = modelId.contains("embedding") ? "embedding" : "chat";
                ProviderModelDTO dto = new ProviderModelDTO();
                dto.setModelKey(modelId);
                dto.setModelName(modelId);
                dto.setModelType(modelType);
                dto.setCapabilities(ModelParamDefaults.inferCapsFromId(modelId, modelType));
                dto.setParams(ModelParamDefaults.inferParamsFromId(modelId, modelType));
                models.add(dto);
            }
            return models;
        } catch (Exception e) { return Collections.emptyList(); }
    }
}
